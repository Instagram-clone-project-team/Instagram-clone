package com.project.Instagram.domain.follow.entity;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.global.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "follows")
public class Follow extends BaseTimeEntity {
    @Id
    @Column(name = "follow_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_member_id")
    private Member followMember;

    @Builder
    public Follow(Member member, Member followMember) {
        this.member = member;
        this.followMember = followMember;
    }
}
