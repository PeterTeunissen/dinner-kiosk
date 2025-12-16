package com.example.kiosk.api.dto;

import jakarta.validation.constraints.*;

public class AssignIdeaRequest {
  @NotNull @Min(1) @Max(7)
  public Integer dayOfWeek;

  @NotNull
  public Long ideaId;

  // optional: archive idea after use
  public Boolean archiveIdea = Boolean.FALSE;
}
