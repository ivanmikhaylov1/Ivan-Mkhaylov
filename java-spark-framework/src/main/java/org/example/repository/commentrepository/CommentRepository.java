package org.example.repository.commentrepository;

import org.example.entity.Article.ArticleId;
import org.example.entity.Comment.Comment;
import org.example.entity.Comment.CommentId;

import java.util.List;

public interface CommentRepository {
  Comment findById(ArticleId articleId, CommentId commentId);

  List<Comment> findAllByArticleId(ArticleId articleId);

  void save(Comment comment);

  void delete(ArticleId articleId, CommentId commentId);

  CommentId generateCommentId();

  void update(Comment comment);
}