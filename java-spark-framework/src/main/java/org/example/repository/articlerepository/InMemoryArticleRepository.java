package org.example.repository.articlerepository;

import org.example.entity.Article.Article;
import org.example.entity.Article.ArticleId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryArticleRepository implements ArticleRepository {
  private final Map<ArticleId, Article> articles = new ConcurrentHashMap<>();
  private final AtomicLong articleIdCounter = new AtomicLong();

  public ArticleId generateArticleId() {
    return new ArticleId(articleIdCounter.incrementAndGet());
  }

  @Override
  public Article findById(ArticleId articleId) {
    return articles.get(articleId);
  }

  @Override
  public List<Article> findAll() {
    return new ArrayList<>(articles.values());
  }

  @Override
  public void save(Article article) {
    articles.put(article.id(), article);
  }

  @Override
  public void delete(ArticleId articleId) {
    articles.remove(articleId);
  }

  @Override
  public void update(Article article) {
    articles.put(article.id(), article);
  }
}