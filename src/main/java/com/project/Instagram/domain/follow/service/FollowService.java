package com.project.Instagram.domain.follow.service;

import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public boolean follow(String followMemberUsername) {
        final Long memberId = securityUtil.getLoginMember().getId();
        final Member member = memberRepository.findById(memberId).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        final Member followMember = memberRepository.findByUsername(followMemberUsername).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getId().equals(followMember.getId())) throw new BusinessException(ErrorCode.FOLLOW_MYSELF_FAIL);
        if (followRepository.existsByMemberIdAndFollowMemberId(member.getId(), followMember.getId())) throw new BusinessException(ErrorCode.FOLLOW_ALREADY_EXIST);

        final Follow follow = new Follow(member, followMember);
        followRepository.save(follow);
        return true;
    }

    @Transactional
    public boolean unfollow(String followMemberUsername) {
        final Long memberId = securityUtil.getLoginMember().getId();
        final Member followMember = memberRepository.findByUsername(followMemberUsername).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (memberId.equals(followMember.getId())) throw  new BusinessException(ErrorCode.UNFOLLOW_MYSELF_FAIL);

        final Follow follow = followRepository.findByMemberIdAndFollowMemberId(memberId, followMember.getId()).orElseThrow(() -> new BusinessException(ErrorCode.UNFOLLOW_FAIL));
        if (follow.getDeletedAt() != null) throw new BusinessException(ErrorCode.FOLLOW_ALREADY_EXIST);
        follow.setDeletedAt(LocalDateTime.now());
//        삭제 로직 회의
//        followRepository.delete(follow);
        return true;
    }
}
