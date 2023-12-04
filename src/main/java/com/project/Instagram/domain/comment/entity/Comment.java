package com.project.Instagram.domain.comment.entity;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.global.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "comments")
@NoArgsConstructor
public class Comment extends BaseTimeEntity {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Column(name = "post_id")
    private long postId;

    @Column(name = "comment_content")
    private String text;

    @Column(name = "parents_comment_id")
    private Long parentsCommentId;

    @Column
    private int replyOrder;

    @Builder
    public Comment(Member writer, long postId, String text, Long parentsCommentId, int replyOrder){
        this.writer=writer;
        this.postId=postId;
        this.text=text;
        this.parentsCommentId=parentsCommentId;
        this.replyOrder=replyOrder;
    }

    public void updateText(String text){this.text = text;}
}
