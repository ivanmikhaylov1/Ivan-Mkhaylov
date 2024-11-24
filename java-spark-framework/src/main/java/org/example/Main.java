package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.controller.ArticleController;
import org.example.controller.ArticleFreemarkerController;
import org.example.controller.CommentController;
import org.example.repository.articlerepository.InMemoryArticleRepository;
import org.example.repository.commentrepository.InMemoryCommentRepository;
import org.example.service.ArticleService;
import org.example.service.CommentService;
import org.example.template.Application;
import org.example.template.TemplateFactory;
import spark.Service;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    Service service = Service.ignite().port(1111);
    service.before((req, res) -> {
      System.out.println("Request: " + req.requestMethod() + " " + req.uri());
    });
    ObjectMapper objectMapper = new ObjectMapper();
    InMemoryArticleRepository inMemoryArticleRepository = new InMemoryArticleRepository();
    InMemoryCommentRepository inMemoryCommentRepository = new InMemoryCommentRepository();
    ArticleService articleService = new ArticleService(inMemoryArticleRepository);
    CommentService commentService = new CommentService(inMemoryCommentRepository, inMemoryArticleRepository);
    Application application = new Application(
        List.of(
            new ArticleController(
                service,
                articleService,
                objectMapper
            ),
            new CommentController(
                service,
                commentService,
                objectMapper
            ),
            new ArticleFreemarkerController(
                service,
                articleService,
                commentService,
                TemplateFactory.freeMarkerEngine()
            )
        )
    );
    application.start();
  }
}