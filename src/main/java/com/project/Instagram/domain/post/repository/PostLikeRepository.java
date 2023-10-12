package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    Optional<PostLike> findByMemberAndPost(Member member, Post post);
    @Query("SELECT pl FROM PostLike pl WHERE pl.post.id = :postId AND pl.post.deletedAt IS NULL AND pl.deletedAt IS NULL")
    Page<PostLike> findByPostId(@Param("postId") Long postId, Pageable pageable);
}
