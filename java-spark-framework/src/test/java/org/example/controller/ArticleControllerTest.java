package java.org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.ArticleController;
import org.example.service.ArticleService;
import org.example.repository.articlerepository.PostgresArticleRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import spark.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ArticleControllerTest {
  private static Service service;
  private static final int PORT = 1111;

  @BeforeAll
  static void setup() {
    service = Service.ignite().port(PORT);
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    ObjectMapper objectMapper = new ObjectMapper();
    PostgresArticleRepository postgresArticleRepository = new PostgresArticleRepository(dataSource);
    ArticleService articleService = new ArticleService(postgresArticleRepository);
    new ArticleController(service, articleService, objectMapper);
    service.awaitInitialization();
  }

  @AfterAll
  static void tearDown() {
    if (service != null) {
      service.stop();
      service.awaitStop();
    }
  }

  @Test
  void testGetArticleNotFound() throws IOException, InterruptedException {
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + PORT + "/api/articles/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(404, response.statusCode());
  }

  @Test
  void testCreateArticle() throws IOException, InterruptedException {
    String requestBody = "{\"name\": \"New Article\", \"tags\": [\"tag1\", \"tag2\"]}";
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://localhost:" + PORT + "/api/articles/1"))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(404, response.statusCode());
  }

  @Test
  void testCreateArticleInvalidData() throws IOException, InterruptedException {
    String requestBody = "{\"name\": \"\", \"tags\": []}";
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://localhost:" + PORT + "/api/articles/1"))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(404, response.statusCode());
  }

  @Test
  void testUpdateArticle() throws IOException, InterruptedException {
    String createRequestBody = "{\"name\": \"Article to Update\", \"tags\": [\"tag1\"]}";
    HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(createRequestBody))
                .uri(URI.create("http://localhost:" + PORT + "/api/articles/1"))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    String updateRequestBody = "{\"name\": \"Updated Article\", \"tags\": [\"tag2\"]}";
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequestBody))
                .uri(URI.create("http://localhost:" + PORT + "/api/articles/1"))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(404, response.statusCode());
  }

  @Test
  void testUpdateArticleNotFound() throws IOException, InterruptedException {
    String updateRequestBody = "{\"name\": \"Updated Article\", \"tags\": [\"tag2\"]}";

    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequestBody))
                .uri(URI.create("http://localhost:" + PORT + "/api/articles/1"))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(404, response.statusCode());
  }

  @Test
  void testDeleteArticle() throws IOException, InterruptedException {
    String createRequestBody = "{\"name\": \"Article to Delete\", \"tags\": [\"tag1\"]}";
    HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(createRequestBody))
                .uri(URI.create("http://localhost:" + PORT + "/api/articles/1"))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:" + PORT + "/api/articles/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(204, response.statusCode());
  }

  @Test
  void testDeleteArticleNotFound() throws IOException, InterruptedException {
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:" + PORT + "/api/articles/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(204, response.statusCode());
  }
}
