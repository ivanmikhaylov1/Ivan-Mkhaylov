package org.example.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.example.repository.articlerepository.InMemoryArticleRepository;
import org.example.repository.commentrepository.InMemoryCommentRepository;
import org.example.service.ArticleService;
import org.example.service.CommentService;
import org.example.template.Application;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Service;

class ApplicationTest {
  private static final int PORT = 1111;
  private Service service;

  @BeforeEach
  void beforeEach() {
    service = Service.ignite().port(PORT);
    InMemoryArticleRepository inMemoryArticleRepository = new InMemoryArticleRepository();
    InMemoryCommentRepository inMemoryCommentRepository = new InMemoryCommentRepository();
    ArticleService articleService = new ArticleService(inMemoryArticleRepository);
    CommentService commentService = new CommentService(inMemoryCommentRepository, inMemoryArticleRepository);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application = new Application(
        List.of(
            new ArticleController(service, articleService, objectMapper),
            new CommentController(service, commentService, objectMapper)
        )
    );
    application.start();
    service.awaitInitialization();
  }

  @AfterEach
  void afterEach() {
    service.stop();
    service.awaitStop();
  }

  @Test
  void testCreateArticle() throws Exception {
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("""
                    { "name": "How to use Postman", "tags": ["API", "Request", "Response"] }"""))
                .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(201, response.statusCode());
  }

  @Test
  void testCreateComment() throws Exception {
    HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("""
                    { "name": "How to use Postman", "tags": ["API", "Request", "Response"] }"""))
                .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("""
                    { "articleID": 1, "text": "Impressive!" }"""))
                .uri(URI.create("http://localhost:%d/api/articles/1/comments".formatted(service.port())))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(500, response.statusCode());
  }

  @Test
  void testUpdateArticle() throws Exception {
    HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("""
                    { "name": "How to use Postman", "tags": ["API", "Request", "Response"] }"""))
                .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString("""
                    { "name": "How to use Spring", "tags": ["API", "Request"] }"""))
                .uri(URI.create("http://localhost:%d/api/articles/1".formatted(service.port())))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(204, response.statusCode());
  }

  @Test
  void testDeleteComment() throws Exception {
    HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("""
                    { "name": "How to use Postman", "tags": ["API", "Request", "Response"] }"""))
                .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("""
                    { "articleID": 1, "text": "Impressive!" }"""))
                .uri(URI.create("http://localhost:%d/api/articles/1/comments".formatted(service.port())))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:%d/api/articles/1/comments/1".formatted(service.port())))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(204, response.statusCode());
  }

  @Test
  void testGetArticle() throws Exception {
    HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("""
                    { "name": "How to use Postman", "tags": ["API", "Request", "Response"] }"""))
                .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:%d/api/articles/1".formatted(service.port())))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(200, response.statusCode());
  }
}
