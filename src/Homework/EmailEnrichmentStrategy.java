package src.Homework;

import java.util.Map;

public class EmailEnrichmentStrategy implements Enrichment {
  private final UserRepository userRepository;

  public EmailEnrichmentStrategy(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Map<String, String> enrich(Map<String, String> input) {
    if (input == null) {
      return null;
    }
    String email = input.get("email");
    if (email != null) {
      User user = userRepository.findByEmail(email);
      if (user == null) {
        user = new User("New", "User");
      }
      input.put("firstName", user.getFirstName());
      input.put("lastName", user.getLastName());
    }
    return input;
  }
}