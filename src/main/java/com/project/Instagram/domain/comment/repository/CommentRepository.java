package com.project.Instagram.domain.comment.repository;

import com.project.Instagram.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Long countCommentsByParentsCommentId(Long parentsCommentId);
    List<Comment> findAllByPostIdAndParentsCommentIdIsNullOrderByCreatedAtAsc(long postId);
    List<Comment> findAllByPostIdAndParentsCommentIdOOrderByReplyOrderAsc(long postId, long parentsCommentId);
    List<Comment> findAllByPostId(long postId);
    boolean existsByIdAndDeletedAtIsNull(long commentId);
}
