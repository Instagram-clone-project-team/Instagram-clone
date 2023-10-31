package com.project.Instagram.domain.comment.entity;

import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.global.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class CommentLike extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public CommentLike(Comment comment, Member member) {
        this.comment = comment;
        this.member = member;
    }
}
