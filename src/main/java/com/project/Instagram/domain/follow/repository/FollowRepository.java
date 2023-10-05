package com.project.Instagram.domain.follow.repository;

import com.project.Instagram.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByMemberIdAndFollowMemberId(Long memberId, Long followMemberId);
}
