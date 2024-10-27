package src.Homework;

import java.util.Map;

public class Message {
  private Map<String, String> content;
  private EnrichmentType enrichmentType; // Убираем static

  public enum EnrichmentType {
    MSISDN,
    EMAIL
  }

  // Конструктор должен быть публичным
  public Message(Map<String, String> content, EnrichmentType enrichmentType) {
    this.content = content;
    this.enrichmentType = enrichmentType;
  }

  public Map<String, String> getContent() {
    return content;
  }

  public EnrichmentType getEnrichmentType() {
    return enrichmentType;
  }
}