package org.example.repository.articlerepository;

import org.example.entity.Article.Article;
import org.example.entity.Article.ArticleId;

import java.util.List;

public interface ArticleRepository {
  Article findById(ArticleId articleId);

  List<Article> findAll();

  void save(Article article);

  void delete(ArticleId articleId);

  void update(Article article);

  ArticleId generateArticleId();
}