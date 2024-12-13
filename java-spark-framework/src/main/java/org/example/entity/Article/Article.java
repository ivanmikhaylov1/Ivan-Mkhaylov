package org.example.entity.Article;

import org.example.entity.Comment.Comment;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public record Article(ArticleId id, String name, LinkedHashSet<String> tags, List<Comment> comments, int version, boolean trending) {
  public Article(ArticleId id, String name, LinkedHashSet<String> tags, List<Comment> comments, int version, boolean trending) {
    this.id = id;
    this.name = name;
    this.tags = new LinkedHashSet<>(tags);
    this.comments = Collections.unmodifiableList(comments);
    this.version = version;
    this.trending = trending;
  }

  @Override
  public String toString() {
    return "Article{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", tags=" + tags +
        ", comments=" + comments +
        ", version=" + version +
        ", trending=" + trending +
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

  public boolean isTrending() {
    return trending;
  }
}