package com.example.kiosk.api;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kiosk.domain.DinnerIdea;
import com.example.kiosk.domain.Ingredient;
import com.example.kiosk.repo.DinnerIdeaRepository;
import com.example.kiosk.repo.WeeklyPlanRepository;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@RestController
@RequestMapping("/api")
public class PrintController {

	private final DinnerIdeaRepository ideaRepo;
	private final WeeklyPlanRepository weeklyPlanRepo;

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PrintController.class);

	public PrintController(DinnerIdeaRepository ideaRepo, WeeklyPlanRepository weeklyPlanRepo) {
		this.ideaRepo = ideaRepo;
		this.weeklyPlanRepo = weeklyPlanRepo;
	}

	public static class PrintRequest {
		public List<IdeaCount> ideaCounts;
		public List<Long> ideaIds;
		public List<ManualMeal> manualMeals;
	}

	public static class IdeaCount {
		public Long ideaId;
		public Integer count = 1;
	}

	public static class ManualMeal {
		public String title;
		public Integer count = 1;
	}

	@PostMapping(path = "/print-shopping", produces = "application/pdf")
	@Transactional(readOnly = true)
	public ResponseEntity<byte[]> printShopping(@RequestBody PrintRequest req) throws Exception {
		// support two payload formats: { ideaCounts:[{ideaId,count},...] } or {
		// ideaIds:[1,2,3] }
		List<IdeaCount> items = new ArrayList<>();
		if (req.ideaCounts != null) {
			items.addAll(req.ideaCounts);
			log.debug("printShopping request ideaCounts={}", req.ideaCounts);
		} else if (req.ideaIds != null) {
			// frontend sent simple selected idea IDs — treat each selection as a single
			// occurrence
			// (do not infer multiplicity from weekly_plan rows to avoid unexpected
			// duplicates)
			for (Long id : req.ideaIds) {
				IdeaCount ic = new IdeaCount();
				ic.ideaId = id;
				ic.count = 1;
				items.add(ic);
			}
			log.debug("printShopping request ideaIds converted to ideaCounts(singleton)={}", items);
		}

		// Resolve each idea once, keep requested count
		List<DinnerIdea> resolved = new ArrayList<>();
		List<Integer> resolvedCount = new ArrayList<>();
		for (IdeaCount it : items) {
			ideaRepo.findById(it.ideaId).ifPresent(d -> {
				resolved.add(d);
				resolvedCount.add(it.count == null ? 1 : it.count);
			});
		}
		log.debug("resolved {} ideas", resolved.size());

		// aggregate ingredients by (description, unit)
		class Agg {
			Double qty = 0.0;
			boolean anyQty = false;
			String unit;
			String desc;
			Set<String> recipes = new TreeSet<>();
		}
		Map<String, Agg> map = new HashMap<>();
		Set<Long> ideasWithNoIngredients = new HashSet<>();

		for (int idx = 0; idx < resolved.size(); idx++) {
			DinnerIdea idea = resolved.get(idx);
			int multiplicity = resolvedCount.get(idx);
			List<Ingredient> ings = idea.getIngredients() == null ? Collections.emptyList() : idea.getIngredients();
			if (ings.isEmpty()) {
				// record that this idea (occurrence) had no ingredients
				// record idea id once per occurrence
				for (int c = 0; c < multiplicity; c++)
					ideasWithNoIngredients.add(idea.getId());
				continue;
			}

			// include manual meals as rows that appear in the Recipes column (ingredient
			// blank)
			if (req.manualMeals != null) {
				for (ManualMeal mm : req.manualMeals) {
					if (mm == null || mm.title == null)
						continue;
					String title = mm.title.trim();
					if (title.isEmpty())
						continue;
					String key = "manual-recipe||" + title.toLowerCase();
					Agg a = map.computeIfAbsent(key, k -> {
						Agg x = new Agg();
						x.desc = ""; // no ingredient
						x.unit = "";
						return x;
					});
					String recipeLabel = title + (mm.count != null && mm.count > 1 ? " (x" + mm.count + ")" : "");
					a.recipes.add(recipeLabel);
				}
			}
			for (Ingredient ing : ings) {
				String desc = (ing.getDescription() == null) ? "" : ing.getDescription().trim();
				String unit = (ing.getUnit() == null) ? "" : ing.getUnit().trim();
				String key = desc.toLowerCase() + "||" + unit;
				Agg a = map.computeIfAbsent(key, k -> {
					Agg x = new Agg();
					x.desc = desc;
					x.unit = unit;
					return x;
				});
				double amt = ing.getAmount() == null ? 0.0 : ing.getAmount();
				a.qty += amt * multiplicity;
				if (amt != 0.0)
					a.anyQty = true;
				// record recipe occurrence count
				a.recipes.add(idea.getTitle() == null ? "—" : idea.getTitle());
			}
		}

		// start PDF
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document doc = new Document();
		PdfWriter.getInstance(doc, baos);
		doc.open();

		doc.add(new Paragraph("Shopping List", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
		doc.add(new Paragraph(" "));

		// table headers: Qty | Unit | Ingredient | Recipes
		PdfPTable table = new PdfPTable(new float[] { 1f, 1f, 4f, 4f });
		table.setWidthPercentage(100);
		Stream.of("Qty", "Unit", "Ingredient", "Recipes").forEach(h -> {
			PdfPCell pc = new PdfPCell(new Paragraph(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
			table.addCell(pc);
		});

		// sort by ingredient description
		List<Agg> aggList = map.values().stream()
				.sorted(Comparator.comparing(a -> a.desc == null ? "" : a.desc.toLowerCase()))
				.collect(Collectors.toList());
		for (Agg a : aggList) {
			table.addCell(a.anyQty ? String.format("%.2f", a.qty) : "");
			table.addCell(a.unit == null ? "" : a.unit);
			table.addCell(a.desc == null ? "" : a.desc);
			table.addCell(String.join(", ", a.recipes));
		}

		doc.add(table);

		if (!ideasWithNoIngredients.isEmpty()) {
			doc.newPage();
			doc.add(new Paragraph("Recipes with no ingredients included:",
					FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
			for (DinnerIdea idea : resolved) {
				if (ideasWithNoIngredients.contains(idea.getId())) {
					doc.add(new Paragraph("- " + (idea.getTitle() == null ? "—" : idea.getTitle())));
				}
			}
		}

		doc.close();

		byte[] pdf = baos.toByteArray();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("inline", "shopping.pdf");
		return ResponseEntity.ok().headers(headers).body(pdf);
	}
}