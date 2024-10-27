package src.Tests;

import src.Homework.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcurrentTest {

  @Test
  void concurrentTest() throws InterruptedException {
    InMemoryUserRepository userRepository = new InMemoryUserRepository() {

      @Override
      public void updateUserByEmail(String email, User user) {}
    };
    userRepository.updateUserByMsisdn("87567513094", new User("Fedor", "Fedorov"));
    EnrichmentService enrichmentService = new EnrichmentService(userRepository);
    enrichmentService.registerStrategy(Message.EnrichmentType.MSISDN, new MSISDNEnrichment(userRepository));
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    CountDownLatch latch = new CountDownLatch(5);
    for (int i = 0; i < 5; i++) {
      executorService.submit(() -> {
        Map<String, String> input = new HashMap<>();
        input.put("gender", "male");
        input.put("height", "200");
        input.put("msisdn", "87567513094");
        Message message = new Message(input, Message.EnrichmentType.MSISDN);
        Message enrichedMessage = enrichmentService.enrich(message);
        assertEquals("Fedor", enrichedMessage.getContent().get("firstName"));
        assertEquals("Fedorov", enrichedMessage.getContent().get("lastName"));
        latch.countDown();
      });
    }
  }
}