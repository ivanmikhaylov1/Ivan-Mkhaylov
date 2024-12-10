package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.controller.ArticleController;
import org.example.controller.ArticleFreemarkerController;
import org.example.controller.CommentController;
import org.example.repository.DataSourceConfig;
import org.example.repository.articlerepository.PostgresArticleRepository;
import org.example.repository.commentrepository.PostgresCommentRepository;
import org.example.service.ArticleService;
import org.example.service.CommentService;
import org.example.template.Application;
import org.example.template.TemplateFactory;
import spark.Service;

import javax.sql.DataSource;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    Service service = Service.ignite().port(1111);
    DataSource dataSource = DataSourceConfig.getDataSource();
    ObjectMapper objectMapper = new ObjectMapper();
    PostgresArticleRepository postgresArticleRepository = new PostgresArticleRepository(dataSource, false);
    PostgresCommentRepository inMemoryCommentRepository = new PostgresCommentRepository(dataSource, false);
    ArticleService articleService = new ArticleService(postgresArticleRepository);
    CommentService commentService = new CommentService(inMemoryCommentRepository);
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