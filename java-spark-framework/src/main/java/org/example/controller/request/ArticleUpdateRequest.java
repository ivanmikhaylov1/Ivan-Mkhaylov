package org.example.controller.request;

import org.example.entity.Comment.Comment;

import java.util.List;
import java.util.Set;

public record ArticleUpdateRequest(long id, String name, Set<String> tags, List<Comment> comments) {
}