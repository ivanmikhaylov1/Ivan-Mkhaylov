package org.example.controller.request;

public record CommentCreateRequest(long id, long articleId, String text) {
}
