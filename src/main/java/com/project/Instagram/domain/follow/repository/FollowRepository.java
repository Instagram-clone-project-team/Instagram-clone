package com.project.Instagram.domain.follow.repository;

import com.project.Instagram.domain.follow.dto.FollowerDto;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> ,FollowRepositoryQuerydsl {
    boolean existsByMemberIdAndFollowMemberId(Long memberId, Long followMemberId);
    Optional<Follow> findByMemberIdAndFollowMemberId(Long memberId, Long followMemberId);

//    Page<Follow> findAllByMember(Long memberId);
//    Page<Follow> findAllByFollowMember(Long memberId);

//    Page<FollowerDto> findByMemberUsernameAndFollowings(String memberUsername, Pageable pageable);

//    @Query("SELECT new com.project.Instagram.domain.follow.dto.FollowerDto(f.member AS member, " +
//            "       CASE WHEN f.member.id IN (SELECT f2.followMember.id FROM Follow f2 WHERE f2.member.id = :loginId) THEN TRUE ELSE FALSE END AS isFollowing, " +
//            "       CASE WHEN f.member.id = :loginId THEN TRUE ELSE FALSE END AS isMe) " +
//            "FROM Follow f " +
//            "WHERE f.followMember.id = :memberId")
//    Page<FollowerDto> findFollowings(Long loginId, Long memberId, Pageable pageable);
}
