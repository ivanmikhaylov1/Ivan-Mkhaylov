package src.Homework;

public interface UserRepository {
  User findByMsisdn(String msisdn);
  void updateUserByMsisdn(String msisdn, User user);

  User findByEmail(String email);
  void updateUserByEmail(String email, User user);
}
