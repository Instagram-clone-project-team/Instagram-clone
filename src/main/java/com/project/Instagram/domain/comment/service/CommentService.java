package com.project.Instagram.domain.comment.service;

import com.project.Instagram.domain.comment.dto.CommentRequest;
import com.project.Instagram.domain.comment.dto.CommentResponse;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.repository.CommentRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.service.PostService;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final SecurityUtil securityUtil;
    private final PostService postService;
    private static final String DELETE_COMMENT = "삭제된 댓글입니다.";

    public void createComment(String text, long postId) {
        Member member = securityUtil.getLoginMember();
        //삭제된 게시글인지 확인
        if (!postService.isExistsAndNotDeleted(postId)) throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        Comment newComment = Comment.builder()
                .writer(member)
                .text(text)
                .postId(postId)
                .parentsCommentId(null)
                .replyOrder(0)
                .build();
        commentRepository.save(newComment);
    }

    public void createReplyComment(String text, long postId, long parentsCommentId) {
        Member member = securityUtil.getLoginMember();
        //유효한 게시글인지 확인
        if (!postService.isExistsAndNotDeleted(postId)) throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        //유효한 부모 댓글인지 확인
        if (!commentRepository.existsByIdAndDeletedAtIsNull(parentsCommentId)) throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        Long count = commentRepository.countCommentsByParentsCommentId(parentsCommentId);
        Comment replyComment = Comment.builder()
                .writer(member)
                .text(text)
                .postId(postId)
                .parentsCommentId(parentsCommentId)
                .replyOrder(count == null ? 1 : (int) (count + 1))
                .build();
        commentRepository.save(replyComment);
    }

    @Transactional
    public void updateComment(long commentId, String text) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        comment.updateText(text);
    }

    @Transactional
    public void deleteComment(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        comment.setDeletedAt(LocalDateTime.now());
        comment.updateText(DELETE_COMMENT);
    }

    public List<CommentResponse> getCommentsByPostId(long postId) {
        //유효한 게시글인지 확인
        if (!postService.isExistsAndNotDeleted(postId)) throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        List<CommentResponse> list = new ArrayList<>();
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        for (Comment c : comments) {
            if (c.getParentsCommentId() != null) continue;
            long parentCommentId = c.getId();
            List<Comment> replies = comments.stream()
                    .filter(e -> e.getParentsCommentId() != null && e.getParentsCommentId() == parentCommentId)
                    .sorted(Comparator.comparing(Comment::getReplyOrder).reversed())
                    .collect(Collectors.toList());
            list.add(CommentResponse.builder().comment(c).replies(replies).build());
        }
        return list;
    }
}
