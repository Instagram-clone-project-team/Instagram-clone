package com.project.Instagram.domain.follow.repository.qeurydsl;

import com.project.Instagram.domain.follow.dto.FollowDto;
import com.project.Instagram.domain.follow.dto.FollowerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface FollowRepositoryQuerydsl {
    Page<FollowerDto> findFollowings(Long loginId, Long memberId, Pageable pageable);

    Page<FollowerDto> findFollowers(Long loginId, Long memberId, Pageable pageable);

    Map<String, List<FollowDto>> findFollowingMemberFollowMap(Long loginId, List<String> usernames);

}

