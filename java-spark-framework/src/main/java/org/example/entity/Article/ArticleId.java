package org.example.entity.Article;

public record ArticleId(long value) {

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof ArticleId articleId)) return false;
    return value == articleId.value;
  }

  @Override
  public String toString() {
    return "ArticleId{" +
        "value=" + value +
        '}';
  }
}