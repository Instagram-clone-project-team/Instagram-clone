package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.CommentLike;
import com.project.Instagram.domain.post.repository.CommentLikeRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final SecurityUtil securityUtil;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    public void commentLike(Long commentId){
        final Comment comment = commentRepository.findById(commentId);
        final Member member = securityUtil.getLoginMember();

        if(commentRepository.findByCommentAndMember(member,comment).isPresent){
            throw new BusinessException(ErrorCode.COMMENTLIKE_ALREADY_EXIST);
        }
        commentRepository.save(new CommentLike(comment,member));
    }
    public void commentUnLike(Long commentID){
        final Comment comment = commentRepository.findById(commentId);
        final Member member = securityUtil.getLoginMember();

        final CommentLike commentlike = commentLikeRepository.findByCommentAndMember(member,comment)
                .orElseThrow(()->new BusinessException(ErrorCode.COMMENTLIKE_NOT_FOUND));
        commentlike.setDeletedAt(LocalDateTime.now());
    }


}
