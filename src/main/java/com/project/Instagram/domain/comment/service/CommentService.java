package com.project.Instagram.domain.comment.service;

import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.repository.CommentRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final SecurityUtil securityUtil;
    private static final String DELETE_COMMENT="삭제된 댓글입니다.";

    public void create(String text, long postId) {
        Member member = securityUtil.getLoginMember();
        //TODO post가 유효한지 확인
        Comment newComment = Comment.builder()
                .writer(member)
                .text(text)
                .postId(postId)
                .parentsCommentId(null)
                .orderNo(0)
                .build();
        log.info("하늘/comment:{}", newComment.getOrderNo());
        commentRepository.save(newComment);
    }

    public void reply(String text, long postId, long parentsCommentId) {
        Member member = securityUtil.getLoginMember();
        //TODO post가 유효한지 확인
        if(!commentRepository.existsById(parentsCommentId))
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        Long count = commentRepository.countCommentsByParentsCommentId(parentsCommentId);
        Comment replyComment = Comment.builder()
                .writer(member)
                .text(text)
                .postId(postId)
                .parentsCommentId(parentsCommentId)
                .orderNo(count == null ? 1 : (int) (count + 1))
                .build();
        commentRepository.save(replyComment);
    }

    @Transactional
    public void update(long commentId, String text){
        Comment comment=commentRepository.findById(commentId).orElseThrow(
                ()->new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        comment.updateText(text);
    }

    @Transactional
    public void delete(long commentId){
        Comment comment=commentRepository.findById(commentId).orElseThrow(
                ()->new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        comment.setDeletedAt(LocalDateTime.now());
        //FIXME 메서드 시그니처 수정하기
        comment.updateText(DELETE_COMMENT);
    }

    public List<Comment> get(long postId){
        log.info("하늘/post-id {}", postId);
        List<Comment> comments=commentRepository.findAllByPostIdAndParentsCommentId(postId, 0);
        return comments;
    }
}
