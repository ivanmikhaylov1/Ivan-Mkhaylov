package src.Homework;

import java.util.Map;

public class MSISDNEnrichment implements Enrichment {
  private final UserRepository userRepository;

  public MSISDNEnrichment(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Map<String, String> enrich(Map<String, String> input) {
    if (input == null) {
      return null;
    }
    String msisdn = input.get("msisdn");
    if (msisdn != null) {
      User user = userRepository.findByMsisdn(msisdn);
      if (user == null) {
         user = new User("New", "User");
      }
      input.put("firstName", user.getFirstName());
      input.put("lastName", user.getLastName());
    }
    return input;
  }
}
