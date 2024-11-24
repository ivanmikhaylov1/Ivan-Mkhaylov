package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.request.CommentCreateRequest;
import org.example.controller.request.CommentUpdateRequest;
import org.example.controller.response.CommentCreateResponse;
import org.example.controller.response.ErrorResponse;
import org.example.entity.Comment.Comment;
import org.example.entity.Comment.CommentId;
import org.example.entity.Article.ArticleId;
import org.example.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.List;
import java.util.NoSuchElementException;

public class CommentController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(CommentController.class);

  private final Service service;
  private final CommentService commentService;
  private final ObjectMapper objectMapper;

  public CommentController(Service service, CommentService commentService, ObjectMapper objectMapper) {
    this.service = service;
    this.commentService = commentService;
    this.objectMapper = objectMapper;
  }

  @Override
  public void initializeEndpoints() {
    createComment();
    findAllComments();
    findCommentById();
    updateComment();
    deleteComment();
  }

  private void createComment() {
    service.post("/api/articles/:articleId/comments", (Request request, Response response) -> {
      response.type("application/json");
      String body = request.body();
      CommentCreateRequest commentCreateRequest = objectMapper.readValue(body, CommentCreateRequest.class);
      try {
        ArticleId articleId = new ArticleId(Long.parseLong(request.params(":articleId")));
        CommentId commentId = commentService.create(articleId, commentCreateRequest.text()).id();
        response.status(201);
        return objectMapper.writeValueAsString(new CommentCreateResponse(commentId.value()));
      } catch (IllegalArgumentException e) {
        LOG.warn("Не удалось создать комментарий", e);
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      } catch (Exception e) {
        LOG.error("Неожиданная ошибка при создании комментария", e);
        response.status(500);
        return objectMapper.writeValueAsString(new ErrorResponse("Внутренняя ошибка сервера"));
      }
    });
  }

  private void findAllComments() {
    service.get("/api/articles/:articleId/comments", (Request request, Response response) -> {
      response.type("application/json");
      try {
        ArticleId articleId = new ArticleId(Long.parseLong(request.params(":articleId")));
        List<Comment> comments = commentService.findAll(articleId);
        return objectMapper.writeValueAsString(comments);
      } catch (NumberFormatException e) {
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse("Неверный формат ID статьи"));
      } catch (Exception e) {
        LOG.error("Неожиданная ошибка при поиске комментариев", e);
        response.status(500);
        return objectMapper.writeValueAsString(new ErrorResponse("Внутренняя ошибка сервера"));
      }
    });
  }

  private void findCommentById() {
    service.get("/api/articles/:articleId/comments/:id", (Request request, Response response) -> {
      response.type("application/json");
      try {
        ArticleId articleId = new ArticleId(Long.parseLong(request.params(":articleId")));
        CommentId commentId = new CommentId(Long.parseLong(request.params(":id")));
        Comment comment = commentService.findById(articleId, commentId);
        return objectMapper.writeValueAsString(comment);
      } catch (NoSuchElementException e) {
        response.status(404);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      } catch (NumberFormatException e) {
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse("Неверный формат ID комментария"));
      } catch (Exception e) {
        LOG.error("Неожиданная ошибка при поиске комментария", e);
        response.status(500);
        return objectMapper.writeValueAsString(new ErrorResponse("Внутренняя ошибка сервера"));
      }
    });
  }

  private void updateComment() {
    service.put("/api/articles/:articleId/comments/:id", (Request request, Response response) -> {
      response.type("application/json");
      try {
        ArticleId articleId = new ArticleId(Long.parseLong(request.params(":articleId")));
        CommentId commentId = new CommentId(Long.parseLong(request.params(":id")));
        String body = request.body();
        CommentUpdateRequest commentUpdateRequest = objectMapper.readValue(body, CommentUpdateRequest.class);
        commentService.update(articleId, commentId, commentUpdateRequest.text());
        response.status(204);
        return "";
      } catch (NoSuchElementException e) {
        response.status (404);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      } catch (IllegalArgumentException e) {
        LOG.warn("Не удалось обновить комментарий", e);
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      } catch (Exception e) {
        LOG.error("Неожиданная ошибка при обновлении комментария", e);
        response.status(500);
        return objectMapper.writeValueAsString(new ErrorResponse("Внутренняя ошибка сервера"));
      }
    });
  }

  private void deleteComment() {
    service.delete("/api/articles/:articleId/comments/:id", (Request request, Response response) -> {
      try {
        ArticleId articleId = new ArticleId(Long.parseLong(request.params(":articleId")));
        CommentId commentId = new CommentId(Long.parseLong(request.params(":id")));
        commentService.delete(articleId, commentId);
        response.status(204);
        return "";
      } catch (NoSuchElementException e) {
        response.status(404);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      } catch (NumberFormatException e) {
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse("Неверный формат ID комментария"));
      } catch (Exception e) {
        LOG.error("Неожиданная ошибка при удалении комментария", e);
        response.status(500);
        return objectMapper.writeValueAsString(new ErrorResponse("Внутренняя ошибка сервера"));
      }
    });
  }
}