package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.request.ArticleCreateRequest;
import org.example.controller.request.ArticleUpdateRequest;
import org.example.controller.response.ArticleCreateResponse;
import org.example.controller.response.ErrorResponse;
import org.example.entity.Article.Article;
import org.example.entity.Article.ArticleId;
import org.example.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ArticleController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(ArticleController.class);

  private final Service service;
  private final ArticleService articleService;
  private final ObjectMapper objectMapper;

  public ArticleController(Service service, ArticleService articleService, ObjectMapper objectMapper) {
    this.service = service;
    this.articleService = articleService;
    this.objectMapper = objectMapper;
    initializeEndpoints();
  }

  @Override
  public void initializeEndpoints() {
    createArticle();
    findAllArticles();
    findArticleById();
    updateArticle();
    deleteArticle();
  }

  private void createArticle() {
    service.post("/api/articles", (Request request, Response response) -> {
      response.type("application/json");
      String body = request.body();
      try {
        ArticleCreateRequest articleCreateRequest = objectMapper.readValue(body, ArticleCreateRequest.class);
        ArticleId articleId = articleService.create(articleCreateRequest.name(), articleCreateRequest.tags()).id();
        response.status(201);
        return objectMapper.writeValueAsString(new ArticleCreateResponse(articleId.value()));
      } catch (IllegalArgumentException e) {
        LOG.warn("Не удалось создать статью", e);
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      } catch (Exception e) {
        LOG.error("Неожиданная ошибка при создании статьи", e);
        response.status(500);
        return objectMapper.writeValueAsString(new ErrorResponse("Внутренняя ошибка сервера"));
      }
    });
  }

  private void findAllArticles() {
    service.get("/api/articles", (Request request, Response response) -> {
      response.type("application/json");
      try {
        List<Article> articles = articleService.findAll();
        return objectMapper.writeValueAsString(articles);
      } catch (Exception e) {
        LOG.error("Ошибка при получении всех статей", e);
        response.status(500);
        return objectMapper.writeValueAsString(new ErrorResponse("Ошибка сервера"));
      }
    });
  }

  private void findArticleById() {
    service.get("/api/articles/:id", (Request request, Response response) -> {
      response.type("application/json");
      try {
        ArticleId articleId = new ArticleId(Long.parseLong(request.params(":id")));
        Optional<Article> article = articleService.findById(articleId);
        return objectMapper.writeValueAsString(article);
      } catch (NoSuchElementException e) {
        response.status(404);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      } catch (NumberFormatException e) {
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse("Неверный формат ID"));
      } catch (Exception e) {
        LOG.error("Ошибка при поиске статьи", e);
        response.status(500);
        return objectMapper.writeValueAsString(new ErrorResponse("Ошибка сервера"));
      }
    });
  }

  private void updateArticle() {
    service.put("/api/articles/:id", (Request request, Response response) -> {
      response.type("application/json");
      try {
        ArticleId articleId = new ArticleId(Long.parseLong(request.params(":id")));
        String body = request.body();
        ArticleUpdateRequest articleUpdateRequest = objectMapper.readValue(body, ArticleUpdateRequest.class);
        articleService.update(articleId, articleUpdateRequest.name(), articleUpdateRequest.tags());
        response.status(204);
        return "";
      } catch (NoSuchElementException e) {
        response.status(404);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      } catch (IllegalArgumentException e) {
        LOG.warn("Не удалось обновить статью", e);
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      } catch (Exception e) {
        LOG.error("Неожиданная ошибка при обновлении статьи", e);
        response.status(500);
        return objectMapper.writeValueAsString(new ErrorResponse("Внутренняя ошибка сервера"));
      }
    });
  }

  private void deleteArticle() {
    service.delete("/api/articles/:id", (Request request, Response response) -> {
      try {
        ArticleId articleId = new ArticleId(Long.parseLong(request.params(":id")));
        articleService.delete(articleId);
        response.status(204);
        return "";
      } catch (NoSuchElementException e) {
        response.status(404);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      } catch (NumberFormatException e) {
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse("Неверный формат ID статьи"));
      } catch (Exception e) {
        LOG.error("Неожиданная ошибка при удалении статьи", e);
        response.status(500);
        return objectMapper.writeValueAsString(new ErrorResponse("Внутренняя ошибка сервера"));
      }
    });
  }
}
