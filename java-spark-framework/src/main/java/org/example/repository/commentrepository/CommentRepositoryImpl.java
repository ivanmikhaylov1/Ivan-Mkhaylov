package org.example.repository.commentrepository;

import org.example.entity.Article.ArticleId;
import org.example.entity.Comment.Comment;
import org.example.entity.Comment.CommentId;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class CommentRepositoryImpl implements CommentRepository {

  private final Jdbi jdbi;

  public CommentRepositoryImpl(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Comment findById(ArticleId articleId, CommentId commentId) {
    return jdbi.withHandle(handle ->
        handle.createQuery("SELECT * FROM comment WHERE article_id = :articleId AND id = :commentId")
            .bind("articleId", articleId.value())
            .bind("commentId", commentId.value())
            .mapToBean(Comment.class)
            .findOne()
            .orElse(null)
    );
  }

  @Override
  public List<Comment> findAllByArticleId(ArticleId articleId) {
    return jdbi.withHandle(handle ->
        handle.createQuery("SELECT * FROM comment WHERE article_id = :articleId")
            .bind("articleId", articleId.value())
            .mapToBean(Comment.class)
            .list()
    );
  }

  @Override
  public void save(Comment comment) {
    jdbi.useHandle(handle ->
        handle.createUpdate("INSERT INTO comment (id, article_id, text) VALUES (:id, :articleId, :text)")
            .bind("id", comment.id().value())
            .bind("articleId", comment.articleId().value())
            .bind("text", comment.text())
            .execute()
    );
  }

  @Override
  public void delete(ArticleId articleId, CommentId commentId) {
    jdbi.useHandle(handle ->
        handle.createUpdate("DELETE FROM comment WHERE article_id = :articleId AND id = :commentId")
            .bind("articleId", articleId.value())
            .bind("commentId", commentId.value())
            .execute()
    );
  }

  @Override
  public void update(Comment comment) {
    jdbi.useHandle(handle ->
        handle.createUpdate("UPDATE comment SET text = :text WHERE article_id = :articleId AND id = :id")
            .bind("id", comment.id().value())
            .bind("articleId", comment.articleId().value())
            .bind("text", comment.text())
            .execute()
    );
  }

  @Override
  public CommentId generateCommentId() {
    return jdbi.withHandle(handle ->
        handle.createQuery("SELECT nextval('comment_id_seq')")
            .mapTo(Long.class)
            .findOne()
            .map(CommentId::new)
            .orElseThrow(() -> new RuntimeException("Failed to generate comment ID"))
    );
  }
}
