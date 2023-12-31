package com.project.Instagram.domain.comment.service;

import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.comment.dto.CommentResponse;
import com.project.Instagram.domain.comment.dto.SimpleComment;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.repository.CommentRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.mention.service.MentionService;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.domain.post.service.HashtagService;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final SecurityUtil securityUtil;
    private final PostRepository postRepository;
    private final AlarmService alarmService;
    private final MentionService mentionService;
    private final HashtagService hashtagService;
    private static final String DELETE_COMMENT = "삭제된 댓글입니다.";

    
    public void createComment(String text, long postId) {
        Member member = securityUtil.getLoginMember();
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        Comment newComment = Comment.builder()
                .writer(member)
                .text(text)
                .postId(postId)
                .parentsCommentId(null)
                .replyOrder(0)
                .build();
        commentRepository.save(newComment);
        hashtagService.registerHashTagOnComment(newComment, newComment.getText());

        if(member!=post.getMember()) alarmService.sendCommentAlarm(AlarmType.COMMENT, member, post.getMember(), post, newComment);
        mentionService.checkMentionsFromComment(member, text, post, newComment);
    }

    public void createReplyComment(String text, long postId, long parentsCommentId) {
        Member member = securityUtil.getLoginMember();
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!postRepository.existsByIdAndDeletedAtIsNull(postId)) throw new BusinessException(ErrorCode.POST_NOT_FOUND);

        if (!commentRepository.existsByIdAndDeletedAtIsNull(parentsCommentId))
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
      
        Long count = commentRepository.countCommentsByParentsCommentId(parentsCommentId);
        Comment newreplyComment = Comment.builder()
                .writer(member)
                .text(text)
                .postId(postId)
                .parentsCommentId(parentsCommentId)
                .replyOrder(count == null ? 1 : (int) (count + 1))
                .build();

        commentRepository.save(newreplyComment);
        if(member!=post.getMember()) alarmService.sendCommentAlarm(AlarmType.COMMENT, member, post.getMember(), post, newreplyComment);
        mentionService.checkMentionsFromComment(member, text, post, newreplyComment);

    }

    @Transactional
    public void updateComment(long commentId, String afterText) {
        Member member = securityUtil.getLoginMember();
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        Post post = postRepository.findByIdAndDeletedAtIsNull(comment.getPostId()).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        if (comment.getWriter() != member) throw new BusinessException(ErrorCode.COMMENT_WRITER_FAIL);
        String beforeText = comment.getText();
        comment.updateText(afterText);
        hashtagService.editHashTagOnComment(comment,beforeText);
        mentionService.checkUpdateMentionsFromComment(member, beforeText, afterText, post, comment);

    }

    @Transactional
    public void deleteComment(long commentId) {
        Member member = securityUtil.getLoginMember();
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        comment.setDeletedAt(LocalDateTime.now());
        if (comment.getWriter() != member) throw new BusinessException(ErrorCode.COMMENT_WRITER_FAIL);
        comment.updateText(DELETE_COMMENT);
    }

    public List<CommentResponse> getCommentsByPostId(long postId) {
        if (!postRepository.existsByIdAndDeletedAtIsNull(postId)) throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        List<CommentResponse> list = new ArrayList<>();
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        for (Comment c : comments) {
            if (c.getParentsCommentId() != null) continue;
            long parentCommentId = c.getId();
            List<SimpleComment> replies = comments.stream()
                    .filter(e -> e.getParentsCommentId() != null && e.getParentsCommentId() == parentCommentId)
                    .sorted(Comparator.comparing(Comment::getReplyOrder).reversed())
                    .map(SimpleComment::new)
                    .collect(Collectors.toList());
            list.add(CommentResponse.builder().comment(new SimpleComment(c)).replies(replies).build());
        }
        return list;
    }
}
