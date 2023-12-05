package com.project.Instagram.domain.comment.service;

import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.repository.CommentRepository;
import com.project.Instagram.domain.member.dto.LikesMemberResponseDto;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.comment.entity.CommentLike;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.comment.repository.CommentLikeRepository;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.project.Instagram.domain.alarm.dto.AlarmType.LIKE_COMMENT;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final SecurityUtil securityUtil;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final AlarmService alarmService;
    @Transactional
    public void createCommentLike(Long commentId){
        //exception 부적절, 아니면 다른 의미가 있는지?
        final Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENTLIKE_NOT_FOUND));
        final Member member = securityUtil.getLoginMember();
        //실제값을 쓰지않고 존재유무만 체크할꺼면 exists가 좋을것같다.
        if(commentLikeRepository.findByCommentAndMember(comment,member).isPresent()){
            throw new BusinessException(ErrorCode.COMMENTLIKE_ALREADY_EXIST);
        }
        commentLikeRepository.save(new CommentLike(comment,member));
        Post post = postRepository.findByIdAndDeletedAtIsNull(comment.getPostId()).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        alarmService.sendCommentAlarm(LIKE_COMMENT,member,comment.getWriter(),post,comment);
    }
    @Transactional
    public void DeleteCommentLike(Long commentId){
        final Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENTLIKE_NOT_FOUND));
        final Member member = securityUtil.getLoginMember();

        final CommentLike commentlike = commentLikeRepository.findByCommentAndMember(comment,member)
                .orElseThrow(()->new BusinessException(ErrorCode.COMMENTLIKE_NOT_FOUND));
        commentLikeRepository.delete(commentlike);
        alarmService.deleteCommentLikeAlarm(LIKE_COMMENT,member,comment.getWriter(),comment);
    }
    @Transactional(readOnly = true)
    public PageListResponse<LikesMemberResponseDto> getCommentLikeUsers(Long commentId, int page, int size) {
        final Pageable pageable = PageRequest.of(page,size);
        securityUtil.checkLoginMember();

        Page<CommentLike> postlikePage = commentLikeRepository.findByCommentIdAndDeletedAtIsNull(commentId,pageable);
        List<LikesMemberResponseDto> LikesMemberResponseDtos = new ArrayList<>();
        postlikePage.forEach(commentLike->{
            LikesMemberResponseDtos.add(new LikesMemberResponseDto(commentLike.getMember()));
        });
        return new PageListResponse<>(LikesMemberResponseDtos,postlikePage);
    }
}
