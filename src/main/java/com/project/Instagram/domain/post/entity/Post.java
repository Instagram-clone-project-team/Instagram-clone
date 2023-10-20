package com.project.Instagram.domain.post.entity;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.global.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "posts")
public class Post extends BaseTimeEntity {
    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String image;

    @Lob
    @Column(name = "post_content")
    private String content;

    @Builder
    public Post(Member member, String image, String content) {
        this.member = member;
        this.image = image;
        this.content = content;
    }

    public void editPost(String content, String image) {
        this.content = content;
        this.image = image;
    }
}
