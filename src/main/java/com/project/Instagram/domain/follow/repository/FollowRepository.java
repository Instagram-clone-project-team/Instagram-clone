package com.project.Instagram.domain.follow.repository;

import com.project.Instagram.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> ,FollowRepositoryQuerydsl {
    Optional<Follow> findByMemberIdAndFollowMemberId(Long memberId, Long followMemberId);
    @Query("SELECT COUNT(f) FROM Follow f JOIN f.member m WHERE m.username = :memberUsername AND f.deletedAt IS NULL")
    int countActiveFollowsByMemberUsername(@Param("memberUsername") String memberUsername);
    @Query("SELECT COUNT(f) FROM Follow f JOIN f.followMember fm WHERE fm.username = :memberUsername AND f.deletedAt IS NULL")
    int countActiveFollowersByMemberUsername(@Param("memberUsername") String memberUsername);
}
