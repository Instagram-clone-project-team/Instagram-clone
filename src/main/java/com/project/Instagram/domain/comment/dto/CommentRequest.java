package com.project.Instagram.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentRequest {
    String text;
    Long postId;
    Long parentsCommentId;
}
