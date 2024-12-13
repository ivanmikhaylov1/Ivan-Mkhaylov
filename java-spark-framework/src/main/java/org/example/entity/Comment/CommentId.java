package org.example.entity.Comment;

public record CommentId(long value) {

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof CommentId commentId)) return false;
    return value == commentId.value;
  }

  @Override
  public String toString() {
    return "CommentId{" +
        "value=" + value +
        '}';
  }
}