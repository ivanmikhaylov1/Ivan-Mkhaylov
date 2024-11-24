package org.example.entity.Article;

import org.example.entity.Comment.Comment;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record Article(ArticleId id, String name, Set<String> tags, List<Comment> comments) {
  public Article(ArticleId id, String name, Set<String> tags, List<Comment> comments) {
    this.id = id;
    this.name = name;
    this.tags = Collections.unmodifiableSet(tags);
    this.comments = Collections.unmodifiableList(comments);
  }

  public Article withName(String newName) {
    return new Article(this.id, newName, this.tags, this.comments);
  }

  public Article withTags(Set<String> newTags) {
    return new Article(this.id, this.name, newTags, this.comments);
  }

  public Article withComments(List<Comment> newComments) {
    return new Article(this.id, this.name, this.tags, newComments);
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