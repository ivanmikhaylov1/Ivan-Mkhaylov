package org.example.repository.commentrepository;

import org.example.entity.Article.ArticleId;
import org.example.entity.Comment.Comment;
import org.example.entity.Comment.CommentId;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostgresCommentRepositoryTest {
  private DataSource dataSource;
  private PostgresCommentRepository commentRepository;

  @BeforeEach
  public void setUp() throws Exception {
    JdbcDataSource h2DataSource = new JdbcDataSource();
    h2DataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    h2DataSource.setUser("sa");
    h2DataSource.setPassword("");
    this.dataSource = h2DataSource;
    commentRepository = new PostgresCommentRepository(dataSource, true);
    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {
      String createTableQuery = """
                CREATE TABLE IF NOT EXISTS articles_test (
                    article_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    article_name VARCHAR(255),
                    tags VARCHAR(255),
                    number_of_comments INT,
                    trending BOOLEAN,
                    version INT
                );
            """;
      stmt.execute(createTableQuery);
      System.out.println("Таблица articles_test создана или уже существует.");
      String insertArticleQuery = """
                INSERT INTO articles_test (article_name, tags, number_of_comments, trending, version)
                VALUES ('Test Article', 'tag1,tag2', 0, false, 0);
            """;
      stmt.executeUpdate(insertArticleQuery);
      System.out.println("Данные статьи успешно вставлены.");
    }
    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {
      String createCommentsTableQuery = """
                CREATE TABLE IF NOT EXISTS comments_test (
                    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    article_id BIGINT,
                    text VARCHAR(255),
                    FOREIGN KEY (article_id) REFERENCES articles_test(article_id)
                );
            """;
      stmt.execute(createCommentsTableQuery);
      System.out.println("Таблица comments_test создана или уже существует.");
    }
  }

  @Test
  void testFindAllEmpty() throws Exception {
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("DELETE FROM comments_test");
    }
    ArticleId articleId = new ArticleId(1L);
    List<Comment> comments = commentRepository.findAllByArticleId(articleId);
    assertNotNull(comments);
    assertTrue(comments.isEmpty(), "Expected empty list, but it wasn't.");
  }

  @Test
  public void testGenerateCommentId() {
    CommentId commentId = commentRepository.generateCommentId();
    assertNotNull(commentId);
  }

  @Test
  public void testFindAllByArticleId() throws Exception {
    ArticleId articleId = new ArticleId(1L);
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("INSERT INTO comments_test (article_id, text) VALUES (1, 'Test Comment 1')");
      statement.execute("INSERT INTO comments_test (article_id, text) VALUES (1, 'Test Comment 2')");
    }
    List<Comment> comments = commentRepository.findAllByArticleId(articleId);
    assertNotNull(comments);
    assertEquals(2, comments.size());
  }

  @Test
  void testDelete() throws Exception {
    ArticleId articleId = new ArticleId(1L);
    CommentId commentId = new CommentId(1L);
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("INSERT INTO comments_test (article_id, text) VALUES (1, 'Test Comment')");
    }
    commentRepository.delete(articleId, commentId);
    Comment deletedComment = commentRepository.findById(articleId, commentId);
    assertNull(deletedComment);
  }

  @Test
  void testUpdateCommentCount() throws Exception {
    ArticleId articleId = new ArticleId(1L);
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("INSERT INTO articles_test (article_name, tags, number_of_comments, trending, version) " +
          "VALUES ('Test Article', 'tag1,tag2', 0, false, 0)");
    }
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("INSERT INTO comments_test (article_id, text) VALUES (1, 'Comment 1')");
    }
    List<Comment> comments = commentRepository.findAllByArticleId(articleId);
    assertEquals(1, comments.size());
  }
}
