package org.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.entity.Article.Article;
import org.example.entity.Article.ArticleId;
import org.example.entity.Comment.Comment;
import org.example.service.ArticleService;
import org.example.service.CommentService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Service;
import spark.template.freemarker.FreeMarkerEngine;

public class ArticleFreemarkerController implements Controller {
  private final Service service;
  private final ArticleService articleService;
  private final CommentService commentService;
  private final FreeMarkerEngine freeMarkerEngine;

  public ArticleFreemarkerController(
      Service service,
      ArticleService articleService, CommentService commentService,
      FreeMarkerEngine freeMarkerEngine
  ) {
    this.service = service;
    this.articleService = articleService;
    this.commentService = commentService;
    this.freeMarkerEngine = freeMarkerEngine;
    initializeEndpoints();
  }

  @Override
  public void initializeEndpoints() {
    getAllArticles();
  }

  private void getAllArticles() {
    service.get("/articles", this::renderAllArticles);
  }

  private Object renderAllArticles(Request request, Response response) {
    response.type("text/html; charset=utf-8");
    try {
      List<Article> articles = articleService.findAll();
      List<Map<String, Object>> articleMapList = articles.stream()
          .map(this::mapArticleToViewModel)
          .collect(Collectors.toList());
      Map<String, Object> model = new HashMap<>();
      model.put("articles", articleMapList);
      return freeMarkerEngine.render(new ModelAndView(model, "index.ftl"));
    } catch (Exception e) {
      response.status(500);
      return "Ошибка при получении статей: " + e.getMessage();
    }
  }

  private Map<String, Object> mapArticleToViewModel(Article article) {
    ArticleId articleId = article.id();
    List<Comment> comments = commentService.findAll(articleId);
    int commentCount = comments.size();
    String tagsString = String.join(", ", article.tags());
    return Map.of(
        "id", articleId,
        "title", article.name(),
        "number", commentCount,
        "tags", tagsString
    );
  }
}