package src.Tests;

import src.Homework.EmailEnrichmentStrategy;
import src.Homework.InMemoryUserRepository;
import src.Homework.MSISDNEnrichment;
import src.Homework.User;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnrichmentTest {
  private InMemoryUserRepository userRepository;
  private MSISDNEnrichment msisdnEnrichment;
  private EmailEnrichmentStrategy emailEnrichment;

  @BeforeEach
  void setUpMsisdn() {
    userRepository = new InMemoryUserRepository() {
      @Override
      public void updateUserByEmail(String email, User user) {}
    };
    userRepository.updateUserByMsisdn("88005553535", new User("Vasya", "Ivanov"));
    msisdnEnrichment = new MSISDNEnrichment(userRepository);
  }

  @Test
  void shouldEnrichWithUserDataMsisdn() {
    Map<String, String> input = new HashMap<>();
    input.put("msisdn", "88005553535");
    Map<String, String> enriched = msisdnEnrichment.enrich(input);
    assertEquals("Vasya", enriched.get("firstName"));
    assertEquals("Ivanov", enriched.get("lastName"));
  }

  @Test
  void shouldNotEnrichIfMsisdnNotFound() {
    Map<String, String> input = new HashMap<>();
    input.put("msisdn", "unknown");
    Map<String, String> enriched = msisdnEnrichment.enrich(input);
    assertEquals("New", enriched.get("firstName"));
    assertEquals("User", enriched.get("lastName"));
  }

  @Test
  void shouldReturnOriginalMapIfMsisdnIsMissing() {
    Map<String, String> input = new HashMap<>();
    input.put("action", "button_click");
    Map<String, String> enriched = msisdnEnrichment.enrich(input);
    assertEquals("button_click", enriched.get("action"));
    assertEquals(null, enriched.get("firstName"));
    assertEquals(null, enriched.get("lastName"));
  }

  @BeforeEach
  void setUpEmail() {
    userRepository = new InMemoryUserRepository() {
      @Override
      public void updateUserByEmail(String email, User user) {}
    };
    userRepository.updateUserByEmail("user@example.com", new User("John", "Doe"));
    emailEnrichment = new EmailEnrichmentStrategy(userRepository);
  }

  @Test
   void shouldEnrichWithUserDataEmail() {
    Map<String, String> input = new HashMap<>();
    input.put("email", "user@example.com");
    Map<String, String> enriched = emailEnrichment.enrich(input);
    assertEquals("New", enriched.get("firstName"));
    assertEquals("User", enriched.get("lastName"));
  }

  @Test
  void shouldNotEnrichIfEmailNotFound() {
    Map<String, String> input = new HashMap<>();
    input.put("email", "unknown@example.com");
    Map<String, String> enriched = emailEnrichment.enrich(input);
    assertEquals("New", enriched.get("firstName"));
    assertEquals("User", enriched.get("lastName"));
  }

  @Test
  void shouldReturnOriginalMapIfEmailIsMissing() {
    Map<String, String> input = new HashMap<>();
    input.put("action", "button_click");
    Map<String, String> enriched = emailEnrichment.enrich(input);
    assertEquals("button_click", enriched.get("action"));
    assertEquals(null, enriched.get("firstName"));
    assertEquals(null, enriched.get("lastName"));
  }
}