package org.example.service;

import org.example.entity.Article.ArticleId;
import org.example.entity.Comment.Comment;
import org.example.entity.Comment.CommentId;
import org.example.repository.commentrepository.CommentRepository;

import java.util.List;
import java.util.NoSuchElementException;

public class CommentService {
  private final CommentRepository commentRepository;

  public CommentService(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  public List<Comment> findAll(ArticleId articleId) {
    return commentRepository.findAllByArticleId(articleId);
  }

  public Comment findById(ArticleId articleId, CommentId commentId) {
    Comment comment = commentRepository.findById(articleId, commentId);
    if (comment == null) {
      throw new NoSuchElementException("Не удалось найти комментарий с id=" + commentId);
    }
    return comment;
  }

  public Comment create(ArticleId articleId, String text) {
    if (text == null || text.isEmpty()) {
      throw new IllegalArgumentException("Текст комментария не может быть пустым");
    }
    CommentId commentId = commentRepository.generateCommentId();
    Comment comment = new Comment(commentId, articleId, text);
    try {
      commentRepository.save(comment);
    } catch (Exception e) {
      throw new RuntimeException("Не удалось сохранить комментарий", e);
    }
    return comment;
  }

  public void update(ArticleId articleId, CommentId commentId, String text) {
    if (text == null || text.isEmpty()) {
      throw new IllegalArgumentException("Текст комментария не может быть пустым");
    }
    Comment existingComment = findById(articleId, commentId);
    Comment updatedComment = existingComment.withText(text);
    try {
      commentRepository.update(updatedComment);
    } catch (Exception e) {
      throw new RuntimeException("Не удалось обновить комментарий с id=" + commentId, e);
    }
  }

  public void delete(ArticleId articleId, CommentId commentId) {
    try {
      commentRepository.delete(articleId, commentId);
    } catch (Exception e) {
      throw new RuntimeException("Не удалось удалить комментарий с id=" + commentId, e);
    }
  }
}