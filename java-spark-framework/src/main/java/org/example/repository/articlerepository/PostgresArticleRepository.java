package org.example.repository.articlerepository;

import org.example.entity.Article.Article;
import org.example.entity.Article.ArticleId;
import org.example.entity.Comment.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PostgresArticleRepository implements ArticleRepository {
  private static final Logger logger = LoggerFactory.getLogger(PostgresArticleRepository.class);
  private final DataSource dataSource;
  private final AtomicLong articleIdCounter = new AtomicLong();

  public PostgresArticleRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public ArticleId generateArticleId() {
    return new ArticleId(articleIdCounter.incrementAndGet());
  }

  @Override
  public Article findById(ArticleId articleId) {
    String query = "SELECT * FROM articles WHERE article_id = ?";
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
    String query = "SELECT * FROM articles";
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
    String query = "INSERT INTO articles (article_name, tags, number_of_comments, trending) VALUES (?, ?, ?, ?)";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setString(1, article.name());
      preparedStatement.setString(2, String.join(",", article.tags()));
      preparedStatement.setInt(3, article.comments().size());
      preparedStatement.setBoolean(4, article.comments().size() >= 3);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error saving article: {}", article.name(), e);
    }
  }

  @Override
  public void delete(ArticleId articleId) {
    String query = "DELETE FROM articles WHERE article_id = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setLong(5, articleId.value());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error deleting article with ID: {}", articleId.value(), e);
    }
  }

  @Override
  public void update(Article article) {
    String query = "UPDATE articles SET article_name = ?, tags = ?, number_of_comments = ?, trending = ? WHERE article_id = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, article.name());
      preparedStatement.setString(2, String.join(",", article.tags()));
      preparedStatement.setInt(3, article.comments().size());
      preparedStatement.setBoolean(4, article.comments().size() >= 3);
      preparedStatement.setLong(5, article.id().value());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error updating article: {}", article.name(), e);
    }
  }

  private Article mapRowToArticle(ResultSet resultSet) throws SQLException {
    ArticleId id = new ArticleId(resultSet.getLong("article_id"));
    String name = resultSet.getString("article_name");
    LinkedHashSet<String> tags = new LinkedHashSet<>(Arrays.asList(resultSet.getString("tags").split(",")));
    List<Comment> comments = new ArrayList<>();
    return new Article(id, name, tags, comments);
  }
}