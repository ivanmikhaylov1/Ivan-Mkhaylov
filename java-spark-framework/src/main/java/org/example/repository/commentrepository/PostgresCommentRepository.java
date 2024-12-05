package org.example.repository.commentrepository;

import org.example.entity.Article.ArticleId;
import org.example.entity.Comment.Comment;
import org.example.entity.Comment.CommentId;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostgresCommentRepository implements CommentRepository {
  private final Map<ArticleId, Map<CommentId, Comment>> commentsByArticle = new ConcurrentHashMap<>();
  private final AtomicLong commentIdCounter = new AtomicLong(1);

  @Override
  public Comment findById(ArticleId articleId, CommentId commentId) {
    Map<CommentId, Comment> comments = commentsByArticle.get(articleId);
    return comments != null ? comments.get(commentId) : null;
  }

  @Override
  public List<Comment> findAllByArticleId(ArticleId articleId) {
    Map<CommentId, Comment> comments = commentsByArticle.get(articleId);
    return comments != null ? List.copyOf(comments.values()) : List.of();
  }

  @Override
  public void save(Comment comment) {
    commentsByArticle
        .computeIfAbsent(comment.articleId(), k -> new ConcurrentHashMap<>())
        .put(comment.id(), comment);
  }

  @Override
  public void delete(ArticleId articleId, CommentId commentId) {
    Map<CommentId, Comment> comments = commentsByArticle.get(articleId);
    if (comments != null) {
      comments.remove(commentId);
    }
  }

  @Override
  public CommentId generateCommentId() {
    return new CommentId(commentIdCounter.getAndIncrement());
  }

  @Override
  public void update(Comment comment) {
    Map<CommentId, Comment> comments = commentsByArticle.get(comment.articleId());
    if (comments != null && comments.containsKey(comment.id())) {
      comments.put(comment.id(), comment);
    } else {
      throw new IllegalArgumentException("Комментарий не найден");
    }
  }
}