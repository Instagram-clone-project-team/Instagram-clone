package com.project.Instagram.domain.comment.dto;

import com.project.Instagram.domain.comment.entity.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleComment {
    private String username;
    private String image;
    private String text;

    @Builder
    public SimpleComment(Comment comment){
        this.username=comment.getWriter().getUsername();
        this.image=comment.getWriter().getImage();
        this.text=comment.getText();
    }
}
