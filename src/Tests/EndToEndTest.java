package src.Tests;

import src.Homework.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EndToEndTest {
  private EnrichmentService enrichmentService;
  private InMemoryUserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository = new InMemoryUserRepository() {
      @Override
      public void updateUserByEmail(String email, User user) {}
    };
    userRepository.updateUserByMsisdn("85859474596", new User("Ivan", "Ivanov"));
    enrichmentService = new EnrichmentService(userRepository);
    enrichmentService.registerStrategy(Message.EnrichmentType.MSISDN, new MSISDNEnrichment(userRepository));
  }

  @Test
  void shouldSucceedEnrichmentInConcurrentEnvironmentSuccessfully() throws InterruptedException {
    List<Message> enrichmentResults = new ArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    CountDownLatch latch = new CountDownLatch(5);
    for (int i = 0; i < 5; i++) {
      executorService.submit(() -> {
        Map<String, String> content = new HashMap<>();
        content.put("msisdn", "85859474596");
        Message message = new Message(content, Message.EnrichmentType.MSISDN);
        Message enrichedMessage = enrichmentService.enrich(message);
        enrichmentResults.add(enrichedMessage);
        latch.countDown();
      });
    }
    latch.await();
    for (Message enrichedMessage : enrichmentResults) {
      assertEquals("Ivan", enrichedMessage.getContent().get("firstName"));
      assertEquals("Ivanov", enrichedMessage.getContent().get("lastName"));
    }
  }
}