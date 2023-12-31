package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    Optional<PostLike> findByMemberAndPost(Member member, Post post);

    Page<PostLike> findByPostIdAndDeletedAtIsNull(@Param("postId") Long postId, Pageable pageable);
}
