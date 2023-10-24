package com.project.Instagram.domain.comment.repository;

import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.comment.entity.CommentLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    Optional<CommentLike> findByCommentAndMember(Comment comment, Member member);

    Page<CommentLike> findByCommentIdAndDeletedAtIsNull(@Param("commentId") Long commentId, Pageable pageable);
}
