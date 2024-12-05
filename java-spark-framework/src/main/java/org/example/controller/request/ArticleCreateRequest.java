package org.example.controller.request;

import org.example.entity.Comment.Comment;

import java.util.LinkedHashSet;
import java.util.List;

public record ArticleCreateRequest(long id, String name, LinkedHashSet<String> tags, List<Comment> comments) {
}
