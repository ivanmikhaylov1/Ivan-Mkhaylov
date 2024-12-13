package org.example.repository.articlerepository;

import org.example.entity.Article.Article;
import org.example.entity.Article.ArticleId;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class ArticleRepositoryImpl implements ArticleRepository {
  private final Jdbi jdbi;

  public ArticleRepositoryImpl(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Article findById(ArticleId articleId) {
    return jdbi.withHandle(handle ->
        handle.createQuery("SELECT * FROM article WHERE id = :id")
            .bind("id", articleId.value())
            .mapToBean(Article.class)
            .findOne()
            .orElse(null)
    );
  }

  @Override
  public List<Article> findAll() {
    return jdbi.withHandle(handle ->
        handle.createQuery("SELECT * FROM article")
            .mapToBean(Article.class)
            .list()
    );
  }

  @Override
  public void save(Article article) {
    jdbi.useHandle(handle ->
        handle.createUpdate("INSERT INTO article (id, title, tags) VALUES (:id, :title, :tags)")
            .bind("id", article.id().value())
            .bind("title", article.name())
            .bind("tags", String.join(",", article.tags()))
            .execute()
    );
  }

  @Override
  public void delete(ArticleId articleId) {
    jdbi.useHandle(handle ->
        handle.createUpdate("DELETE FROM article WHERE id = :id")
            .bind("id", articleId.value())
            .execute()
    );
  }

  @Override
  public void update(Article article) {
    jdbi.useHandle(handle ->
        handle.createUpdate("UPDATE article SET title = :title, tags = :tags WHERE id = :id")
            .bind("id", article.id().value())
            .bind("title", article.name())
            .bind("tags", String.join(",", article.tags()))
            .execute()
    );
  }

  @Override
  public ArticleId generateArticleId() {
    return new ArticleId(System.currentTimeMillis());
  }

  @Override
  public void updateTrending(ArticleId articleId) {
    jdbi.useHandle(handle ->
        handle.createUpdate("UPDATE article SET trending = true WHERE id = :id")
            .bind("id", articleId.value())
            .execute()
    );
  }
}
