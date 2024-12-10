package org.example.repository.articlerepository;

import org.example.entity.Article.Article;
import org.example.entity.Article.ArticleId;
import org.example.entity.Comment.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class PostgresArticleRepository implements ArticleRepository {
  private static final Logger logger = LoggerFactory.getLogger(PostgresArticleRepository.class);
  private final DataSource dataSource;
  private final AtomicLong articleIdCounter = new AtomicLong();
  private final boolean isTestMode;

  public PostgresArticleRepository(DataSource dataSource, boolean isTestMode) {
    this.dataSource = dataSource;
    this.isTestMode = isTestMode;
  }

  private String modifyTableName(String sql) {
    if (isTestMode && sql.contains("articles")) {
      return sql.replace("articles", "articles_test");
    }
    return sql;
  }

  @Override
  public ArticleId generateArticleId() {
    return new ArticleId(articleIdCounter.incrementAndGet());
  }

  @Override
  public Article findById(ArticleId articleId) {
    String query = "SELECT * " +
        "FROM articles WHERE article_id = ?";
    query = modifyTableName(query);
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setLong(1, articleId.value());
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return mapRowToArticle(resultSet);
      }
    } catch (SQLException e) {
      logger.error("Error finding article by ID: {}", articleId.value(), e);
    }
    return null;
  }

  @Override
  public List<Article> findAll() {
    List<Article> articles = new ArrayList<>();
    String query = "SELECT * " +
        "FROM articles";
    query = modifyTableName(query);
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query);
         ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        articles.add(mapRowToArticle(resultSet));
      }
    } catch (SQLException e) {
      logger.error("Error finding all articles", e);
    }
    return articles;
  }

  @Override
  public void save(Article article) {
    String query = "INSERT INTO articles (article_name, tags, number_of_comments, trending, version) VALUES (?, ?, ?, ?, ?)";
    query = modifyTableName(query);
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setString(1, article.name());
      preparedStatement.setString(2, String.join(",", article.tags()));
      preparedStatement.setInt(3, article.comments().size());
      preparedStatement.setBoolean(4, article.comments().size() >= 3);
      preparedStatement.setInt(5, 0);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error saving article: {}", article.name(), e);
    }
  }

  @Override
  public void delete(ArticleId articleId) {
    Article article = findById(articleId);
    String query = "DELETE FROM articles WHERE article_id = ? AND version = ?";
    query = modifyTableName(query);
    if (article != null) {
      int version = article.version();
      executeWithOptimisticLocking(
          query,
          ps -> {
            ps.setLong(1, articleId.value());
            ps.setInt(2, version);
          },
          "deleting article with ID: " + articleId.value()
      );
    } else {
      throw new RuntimeException("Article not found");
    }
  }


  @Override
  public void update(Article article) {
    String query = "UPDATE articles SET article_name = ?, tags = ?, number_of_comments = ?, trending = ?, version = version + 1 WHERE article_id = ? AND version = ?";
    query = modifyTableName(query);
    executeWithOptimisticLocking(
        query,
        ps -> {
          ps.setString(1, article.name());
          ps.setString(2, String.join(",", article.tags()));
          ps.setInt(3, article.comments().size());
          ps.setBoolean(4, article.comments().size() >= 3);
          ps.setLong(5, article.id().value());
          ps.setInt(6, article.version());
        },
        "updating article: " + article.name()
    );
  }

  @Override
  public void updateTrending(ArticleId articleId) {
    String selectQuery = "SELECT number_of_comments, version FROM articles WHERE article_id = ?";
    selectQuery = modifyTableName(selectQuery);
    String updateQuery = "UPDATE articles SET trending = ?, version = version + 1 WHERE article_id = ? AND version = ?";
    updateQuery = modifyTableName(updateQuery);
    try (Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(false);
      try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
        selectStatement.setLong(1, articleId.value());
        ResultSet resultSet = selectStatement.executeQuery();
        if (resultSet.next()) {
          int numberOfComments = resultSet.getInt("number_of_comments");
          int currentVersion = resultSet.getInt("version");
          boolean trending = numberOfComments >= 3;
          executeWithOptimisticLocking(
              updateQuery,
              ps -> {
                ps.setBoolean(1, trending);
                ps.setLong(2, articleId.value());
                ps.setInt(3, currentVersion);
              },
              "updating trending for article with ID: " + articleId.value()
          );
        }
        connection.commit();
      } catch (SQLException e) {
        connection.rollback();
        throw new RuntimeException("Error updating trending status", e);
      }
    } catch (SQLException e) {
      logger.error("Error connecting to the database for trending update", e);
    }
  }

  private void executeWithOptimisticLocking(String query, ThrowingConsumer<PreparedStatement> preparer, String actionDescription) {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparer.accept(preparedStatement);
      int rowsAffected = preparedStatement.executeUpdate();
      if (rowsAffected == 0) {
        throw new RuntimeException("Optimistic locking failed: " + actionDescription);
      }
    } catch (SQLException e) {
      logger.error("Error " + actionDescription, e);
    }
  }

  private Article mapRowToArticle(ResultSet resultSet) throws SQLException {
    ArticleId id = new ArticleId(resultSet.getLong("article_id"));
    String name = resultSet.getString("article_name");
    LinkedHashSet<String> tags = new LinkedHashSet<>(Arrays.asList(resultSet.getString("tags").split(",")));
    List<Comment> comments = new ArrayList<>();
    int version = resultSet.getInt("version");
    return new Article(id, name, tags, comments, version);
  }
}
