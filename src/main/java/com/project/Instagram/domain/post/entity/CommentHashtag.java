package com.project.Instagram.domain.post.entity;

import com.project.Instagram.global.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class CommentHashtag extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashTag_id")
    private Hashtag hashtag;

    @Builder
    public CommentHashtag(Hashtag hashtag,Comment comment){
        this.comment = comment;
        this.hashtag =hashtag;
    }
}
