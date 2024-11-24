package org.example.entity.Comment;

import org.example.entity.Article.ArticleId;

import java.util.Objects;

public record Comment(CommentId id, ArticleId articleId, String text) {

  public Comment withText(String newText) {
    return new Comment(this.id, this.articleId, newText);
  }

  @Override
  public String toString() {
    return "Comment{" +
        "id=" + id +
        ", articleId=" + articleId +
        ", text='" + text + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    Comment comment = (Comment) object;
    return Objects.equals(id, comment.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}