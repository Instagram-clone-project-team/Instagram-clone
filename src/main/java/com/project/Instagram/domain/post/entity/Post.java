package com.project.Instagram.domain.post.entity;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.global.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "post_image")
    private String image;

    @Lob
    @Column(name = "post_content")
    private String content;

    private int likeCount;
    @Builder
    public Post(Member member, String image, String content) {
        this.member = member;
        this.image = image;
        this.content = content;
    }

    public void updatePost(String content, String image) {
        this.content = content;
        this.image = image;
    }
    public void upLikeCount(Post post){this.likeCount=likeCount+1;}
    public void downLikeCount(Post post){this.likeCount=likeCount-1;}
    @OneToMany(mappedBy = "post")
    private List<PostLike> postLikes = new ArrayList<>();


}
