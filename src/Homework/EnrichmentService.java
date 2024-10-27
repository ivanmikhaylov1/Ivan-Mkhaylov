package src.Homework;

import java.util.HashMap;
import java.util.Map;

public class EnrichmentService {
  private final UserRepository userRepository;
  private final Map<Message.EnrichmentType, Enrichment> strategies = new HashMap<>();

  public EnrichmentService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void registerStrategy(Message.EnrichmentType type, Enrichment enrichment) {
    strategies.put(type, enrichment);
  }

  public Message enrich(Message message) {
    if (message == null || message.getContent() == null) {
      return message;
    }
    Enrichment enrichmentStrategy = strategies.get(message.getEnrichmentType());
    if (enrichmentStrategy != null) {
      Map<String, String> enrichedContent = enrichmentStrategy.enrich(message.getContent());
      return new Message(enrichedContent, message.getEnrichmentType());
    }
    return message;
  }
}