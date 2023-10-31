package com.project.Instagram.domain.follow.service;

import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.follow.dto.FollowerDto;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.Instagram.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final AlarmService alarmService;
    private final SecurityUtil securityUtil;

    @Transactional
    public boolean follow(String followMemberUsername) {
        final Long memberId = securityUtil.getLoginMember().getId();
        final Member member = memberRepository.findById(memberId).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
        final Member followMember = memberRepository.findByUsername(followMemberUsername).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));

        if (member.getId().equals(followMember.getId())) throw new BusinessException(FOLLOW_MYSELF_FAIL);

        if (followRepository.existsByMemberIdAndFollowMemberId(member.getId(), followMember.getId())) throw new BusinessException(FOLLOW_ALREADY_EXIST);

        final Follow follow = new Follow(member, followMember);
        followRepository.save(follow);
        member.increaseFollowingCount();
        followMember.increaseFollowerCount();
        alarmService.sendFollowAlarm(member, followMember, follow);
        return true;
    }

    @Transactional
    public boolean unfollow(String followMemberUsername) {
        final Long memberId = securityUtil.getLoginMember().getId();
        final Member member = memberRepository.findById(memberId).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
        final Member followMember = memberRepository.findByUsername(followMemberUsername).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));

        if (memberId.equals(followMember.getId())) throw new BusinessException(UNFOLLOW_MYSELF_FAIL);

        final Follow follow = followRepository.findByMemberIdAndFollowMemberId(memberId, followMember.getId()).orElseThrow(() -> new BusinessException(UNFOLLOW_FAIL));
        followRepository.delete(follow);
        member.decreaseFollowingCount();
        followMember.decreaseFollowerCount();
        alarmService.deleteFollowAlarm(member, followMember, follow);
        return true;
    }

    @Transactional(readOnly = true)
    public PageListResponse<FollowerDto> getFollowings(String memberUsername, int page, int size) {
        final Long memberId = securityUtil.getLoginMember().getId();
        final Member member = memberRepository.findByUsername(memberUsername).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
        Page<FollowerDto> pages = followRepository.findFollowings(memberId, member.getId(), PageRequest.of(page, size));
        return new PageListResponse<>(pages.getContent(), pages);
    }

    @Transactional(readOnly = true)
    public PageListResponse<FollowerDto> getFollowers(String memberUsername, int page, int size) {
        final Long memberId = securityUtil.getLoginMember().getId();
        final Member member = memberRepository.findByUsername(memberUsername).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
        Page<FollowerDto> pages = followRepository.findFollowers(memberId, member.getId(), PageRequest.of(page, size));
        return new PageListResponse<>(pages.getContent(), pages);
    }

    @Transactional(readOnly = true)
    public int getFollowingCount(String memberUsername) {
        memberRepository.findByUsername(memberUsername).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
        return followRepository.countActiveFollowsByMemberUsername(memberUsername);
    }

    @Transactional(readOnly = true)
    public int getFollowerCount(String memberUsername) {
        memberRepository.findByUsername(memberUsername).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
        return followRepository.countActiveFollowersByMemberUsername(memberUsername);

    }

    public List<Long> getFollowedMemberIds(Long memberId) {
        List<Follow> follows = followRepository.findByMemberId(memberId);
        return follows.stream()
                .map(follow -> follow.getFollowMember().getId())
                .collect(Collectors.toList());

    }
}
