package org.example.repository.articlerepository;

import java.sql.SQLException;

public interface ThrowingConsumer<T> {
  void accept(T t) throws SQLException;
}
