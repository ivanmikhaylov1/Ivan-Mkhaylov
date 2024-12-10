package org.example.repository.articlerepository;

import org.example.entity.Article.Article;
import org.example.entity.Article.ArticleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedHashSet;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PostgresArticleRepositoryTest {

  private DataSource dataSource;
  private PostgresArticleRepository articleRepository;

  @BeforeEach
  public void setUp() {
    dataSource = mock(DataSource.class);
    articleRepository = new PostgresArticleRepository(dataSource, false); // Включаем использование обычной таблицы
  }

  @Test
  void testGenerateArticleId() {
    long expectedId = 1L;
    ArticleId articleId = articleRepository.generateArticleId();
    assertEquals(expectedId, articleId.value());
  }

  @Test
  void testFindById() throws SQLException {
    ArticleId articleId = new ArticleId(1L);
    String sql = "SELECT * FROM articles WHERE article_id = ?";

    // Мокируем результат SQL-запроса
    Connection connection = mock(Connection.class);
    PreparedStatement preparedStatement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true);
    when(resultSet.getLong("article_id")).thenReturn(1L);
    when(resultSet.getString("article_name")).thenReturn("Test Article");
    when(resultSet.getString("tags")).thenReturn("tag1,tag2");
    when(resultSet.getInt("version")).thenReturn(0);

    // Мокируем поведение articleRepository
    Article article = articleRepository.findById(articleId);
    assertNotNull(article);
    assertEquals("Test Article", article.name());
    assertTrue(article.tags() instanceof LinkedHashSet);
    assertTrue(article.tags().contains("tag1"));
  }

  @Test
  void testFindAll() throws SQLException {
    String sql = "SELECT * FROM articles";
    Article article1 = new Article(new ArticleId(1L), "Test Article 1", new LinkedHashSet<>(List.of("tag1")), List.of(), 0);
    Article article2 = new Article(new ArticleId(2L), "Test Article 2", new LinkedHashSet<>(List.of("tag2")), List.of(), 0);

    // Мокируем результат SQL-запроса
    Connection connection = mock(Connection.class);
    PreparedStatement preparedStatement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true, true, false);  // Два результата
    when(resultSet.getLong("article_id")).thenReturn(1L, 2L);
    when(resultSet.getString("article_name")).thenReturn("Test Article 1", "Test Article 2");
    when(resultSet.getString("tags")).thenReturn("tag1", "tag2");
    when(resultSet.getInt("version")).thenReturn(0, 0);

    // Мокируем поведение articleRepository
    List<Article> articles = articleRepository.findAll();
    assertEquals(2, articles.size());
    assertEquals("Test Article 1", articles.get(0).name());
    assertEquals("Test Article 2", articles.get(1).name());
    assertTrue(articles.get(0).tags() != null);
    assertTrue(articles.get(1).tags() != null);
  }

  @Test
  void testSave() throws SQLException {
    Article article = new Article(new ArticleId(1L), "Test Article", new LinkedHashSet<>(List.of("tag1", "tag2")), List.of(), 0);
    String sql = "INSERT INTO articles (article_name, tags, number_of_comments, trending, version) VALUES (?, ?, ?, ?, ?)";
    Connection connection = mock(Connection.class);
    PreparedStatement preparedStatement = mock(PreparedStatement.class);

    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)).thenReturn(preparedStatement);

    // Выполнение запроса
    articleRepository.save(article);

    // Проверка, что метод executeUpdate был вызван
    verify(preparedStatement, times(1)).executeUpdate();
  }

  @Test
  void testDelete() throws SQLException {
    ArticleId articleId = new ArticleId(1L);
    String sql = "DELETE FROM articles WHERE article_id = ? AND version = ?";

    // Мокируем SQL-операцию
    Connection connection = mock(Connection.class);
    PreparedStatement preparedStatement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true);
    when(resultSet.getInt("version")).thenReturn(0);

    // Мокируем поведение articleRepository
    articleRepository.delete(articleId);

    // Проверка, что метод executeUpdate был вызван
    verify(preparedStatement, times(1)).executeUpdate();
  }

  @Test
  void testUpdateTrending() throws SQLException {
    ArticleId articleId = new ArticleId(1L);
    String selectQuery = "SELECT number_of_comments, version FROM articles WHERE article_id = ?";
    String updateQuery = "UPDATE articles SET trending = ?, version = version + 1 WHERE article_id = ? AND version = ?";

    // Мокируем SQL-операцию
    Connection connection = mock(Connection.class);
    PreparedStatement selectStatement = mock(PreparedStatement.class);
    PreparedStatement updateStatement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(selectQuery)).thenReturn(selectStatement);
    when(selectStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true);
    when(resultSet.getInt("number_of_comments")).thenReturn(5);
    when(resultSet.getInt("version")).thenReturn(0);

    when(connection.prepareStatement(updateQuery)).thenReturn(updateStatement);

    // Мокируем поведение articleRepository
    articleRepository.updateTrending(articleId);

    // Проверка, что метод executeUpdate был вызван для обновления
    verify(updateStatement, times(1)).executeUpdate();
  }
}
