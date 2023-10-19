package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    Optional<CommentLike> findByCommentAndMember(Member member, Comment comment);
}
