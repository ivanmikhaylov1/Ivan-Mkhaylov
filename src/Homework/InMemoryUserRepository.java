package src.Homework;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class InMemoryUserRepository implements UserRepository {
  private final Map<String, User> user = new ConcurrentHashMap<>();

  @Override
  public User findByMsisdn(String msisdn) {
    return user.get(msisdn);
  }

  public User findByEmail(String email) {
    return user.get(email);
  }

  @Override
  public void updateUserByMsisdn(String msisdn, User user) {
    this.user.put(msisdn, user);
  }
}