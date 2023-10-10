package com.project.Instagram.domain.follow.repository;

import com.project.Instagram.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> ,FollowRepositoryQuerydsl {
    Optional<Follow> findByMemberIdAndFollowMemberId(Long memberId, Long followMemberId);
}