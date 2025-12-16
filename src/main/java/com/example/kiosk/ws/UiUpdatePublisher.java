package com.example.kiosk.ws;

import java.time.Instant;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class UiUpdatePublisher {

  private final SimpMessagingTemplate messaging;

  public UiUpdatePublisher(SimpMessagingTemplate messaging) {
    this.messaging = messaging;
  }

  public void publishRefresh(String reason) {
    messaging.convertAndSend("/topic/ui", (Map<String,String>) Map.of(
      "type", "REFRESH",
      "reason", reason,
      "ts", Instant.now().toString()
    ));
  }
}
