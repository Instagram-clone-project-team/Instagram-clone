package com.project.Instagram.domain.comment.dto;

import lombok.Getter;

@Getter
public class CommentRequest {
    String text;
    Long postId;
    Long parentsCommentId;
}
