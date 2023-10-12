package com.project.Instagram.domain.comment.dto;

import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class CommentResponse {
    private Comment comment;
    private List<Comment> replies;

    @Builder
    public CommentResponse(Comment comment, List<Comment> replies){
        this.comment=comment;
        this.replies=replies;
    }
}
