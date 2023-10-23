package com.project.Instagram.domain.alarm.entity;

import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostLike;
import com.project.Instagram.global.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "alarms")
public class Alarm extends BaseTimeEntity {

    @Id
    @Column(name = "alarm_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alarm_type")
    @Enumerated(EnumType.STRING)
    private AlarmType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_agent_id")
    private Member agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_target_id")
    private Member target;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coment_id")
    private Comment comment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_id")
    private Follow follow;

    @Builder
    public Alarm(AlarmType type, Member agent, Member target, Post post, Comment comment, Follow follow) {
        this.type = type;
        this.agent = agent;
        this.target = target;
        this.post = post;
        this.comment = comment;
        this.follow = follow;
    }

}
