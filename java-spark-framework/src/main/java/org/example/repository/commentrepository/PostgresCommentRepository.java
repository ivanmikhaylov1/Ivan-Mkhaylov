package org.example.repository.commentrepository;

import org.example.entity.Article.ArticleId;
import org.example.entity.Comment.Comment;
import org.example.entity.Comment.CommentId;
import org.example.repository.articlerepository.PostgresArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PostgresCommentRepository implements CommentRepository {
  private static final Logger logger = LoggerFactory.getLogger(PostgresCommentRepository.class);
  private final DataSource dataSource;
  private final AtomicLong commentIDCounter = new AtomicLong();

  public PostgresCommentRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Comment findById(ArticleId articleId, CommentId commentId) {
    String query = "SELECT * FROM comments WHERE article_id = ? AND comment_id = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setLong(1, articleId.value());
      preparedStatement.setLong(2, commentId.value());
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return mapRowToComment(resultSet);
      }
    } catch (SQLException e) {
      logger.error("Error finding comment by ID: articleId={}, commentId={}", articleId.value(), commentId.value(), e);
    }
    return null;
  }

  @Override
  public List<Comment> findAllByArticleId(ArticleId articleId) {
    List<Comment> comments = new ArrayList<>();
    String query = "SELECT * FROM comments WHERE article_id = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setLong(1, articleId.value());
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        comments.add(mapRowToComment(resultSet));
      }
    } catch (SQLException e) {
      logger.error("Error finding all comments for article ID: {}", articleId.value(), e);
    }
    return comments;
  }

  @Override
  public void save(Comment comment) {
    String query = "INSERT INTO comments (article_id, text) VALUES (?, ?)";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setLong(1, comment.articleId().value());
      preparedStatement.setString(2, comment.text());
      preparedStatement.executeUpdate();
      updateCommentCount(comment.articleId(), 1);
    } catch (SQLException e) {
      logger.error("Error saving comment: {}", comment.text(), e);
    }
  }

  @Override
  public void delete(ArticleId articleId, CommentId commentId) {
    String query = "DELETE FROM comments WHERE article_id = ? AND comment_id = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setLong(1, articleId.value());
      preparedStatement.setLong(2, commentId.value());
      preparedStatement.executeUpdate();
      updateCommentCount(articleId, -1);
    } catch (SQLException e) {
      logger.error("Error deleting comment with ID: articleId={}, commentId={}", articleId.value(), commentId.value(), e);
    }
  }

  @Override
  public void update(Comment comment) {
    String query = "UPDATE comments SET text = ? WHERE article_id = ? AND comment_id = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, comment.text());
      preparedStatement.setLong(2, comment.articleId().value());
      preparedStatement.setLong(3, comment.id().value());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error updating comment: {}", comment.text(), e);
    }
  }

  @Override
  public CommentId generateCommentId() {
    return new CommentId(commentIDCounter.incrementAndGet());
  }

  private Comment mapRowToComment(ResultSet resultSet) throws SQLException {
    ArticleId articleId = new ArticleId(resultSet.getLong("article_id"));
    CommentId commentId = new CommentId(resultSet.getLong("comment_id"));
    String text = resultSet.getString("text");
    return new Comment(commentId, articleId, text);
  }

  private void updateCommentCount(ArticleId articleId, int delta) {
    String updateCountQuery = "UPDATE articles SET number_of_comments = number_of_comments + ? WHERE article_id = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(updateCountQuery)) {
      preparedStatement.setInt(1, delta);
      preparedStatement.setLong(2, articleId.value());
      preparedStatement.executeUpdate();
      PostgresArticleRepository articleRepository = new PostgresArticleRepository(dataSource);
      articleRepository.updateTrending(articleId);
    } catch (SQLException e) {
      logger.error("Error updating comment count or trending for article ID: {}", articleId.value(), e);
    }
  }
}
