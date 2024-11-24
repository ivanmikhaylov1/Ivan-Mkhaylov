package org.example.service;

import org.example.entity.Article.Article;
import org.example.entity.Article.ArticleId;
import org.example.repository.articlerepository.ArticleRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class ArticleService {
  private final ArticleRepository articleRepository;

  public ArticleService(ArticleRepository articleRepository) {
    this.articleRepository = articleRepository;
  }

  public List<Article> findAll() {
    return articleRepository.findAll();
  }

  public Article findById(ArticleId articleId) {
    Article article = articleRepository.findById(articleId);
    if (article == null) {
      throw new NoSuchElementException("Не удалось найти статью с id=" + articleId);
    }
    return article;
  }

  public Article create(String name, Set<String> tags) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Имя статьи не может быть пустым");
    }
    ArticleId articleId = articleRepository.generateArticleId();
    Article article = new Article(articleId, name, tags, List.of());
    try {
      articleRepository.save(article);
    } catch (Exception e) {
      throw new RuntimeException("Не удалось создать статью", e);
    }
    return article;
  }

  public void update(ArticleId id, String name, Set<String> tags) {
    Article article = articleRepository.findById(id);
    if (article == null) {
      throw new NoSuchElementException("Не удалось найти статью с id=" + id);
    }
    Article updatedArticle = new Article(article.id(), name, tags, article.comments());
    try {
      articleRepository.update(updatedArticle);
    } catch (Exception e) {
      throw new RuntimeException("Не удалось обновить статью с id=" + id, e);
    }
  }

  public void delete(ArticleId articleId) {
    try {
      articleRepository.delete(articleId);
    } catch (Exception e) {
      throw new RuntimeException("Не удалось удалить статью с id=" + articleId, e);
    }
  }
}