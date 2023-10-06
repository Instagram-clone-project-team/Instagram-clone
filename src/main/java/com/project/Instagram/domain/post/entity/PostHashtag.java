package com.project.Instagram.domain.post.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "hashtag_posts")
public class PostHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_postId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostHashtag(Hashtag hashtag, Post post) {
        this.hashtag = hashtag;
        this.post = post;
    }
}
