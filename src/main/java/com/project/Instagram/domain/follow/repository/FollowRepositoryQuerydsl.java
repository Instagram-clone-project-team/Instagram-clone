package com.project.Instagram.domain.follow.repository;

import com.project.Instagram.domain.follow.dto.FollowerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowRepositoryQuerydsl {
    Page<FollowerDto> findFollowings(Long loginId, Long memberId, Pageable pageable);

    Page<FollowerDto> findFollowers(Long loginId, Long memberId, Pageable pageable);
}

