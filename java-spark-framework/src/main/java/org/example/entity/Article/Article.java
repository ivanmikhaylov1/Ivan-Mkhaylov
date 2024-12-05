package org.example.entity.Article;

import org.example.entity.Comment.Comment;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public record Article(ArticleId id, String name, LinkedHashSet<String> tags, List<Comment> comments) {

  public Article(ArticleId id, String name, LinkedHashSet<String> tags, List<Comment> comments) {
    this.id = id;
    this.name = name;
    this.tags = new LinkedHashSet<>(tags);
    this.comments = Collections.unmodifiableList(comments);
  }

  @Override
  public String toString() {
    return "Article{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", tags=" + tags +
        ", comments=" + comments +
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
    Article article = (Article) object;
    return Objects.equals(id, article.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}