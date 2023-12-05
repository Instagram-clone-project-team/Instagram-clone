package com.project.Instagram.domain.comment.service;

import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.entity.CommentLike;
import com.project.Instagram.domain.comment.repository.CommentLikeRepository;
import com.project.Instagram.domain.comment.repository.CommentRepository;
import com.project.Instagram.domain.member.dto.LikesMemberResponseDto;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.project.Instagram.global.error.ErrorCode.COMMENTLIKE_NOT_FOUND;
import static com.project.Instagram.global.error.ErrorCode.COMMENT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceTest {
    @InjectMocks
    CommentLikeService commentLikeService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentLikeRepository commentLikeRepository;
    @Mock
    AlarmService alarmService;
    @Mock
    SecurityUtil securityUtil;

    // 윤영
    @Nested
    class DeleteCommentLike {
        @Test
        @DisplayName("deleteCommentLike() 성공")
        void deleteCommentLikeSuccess() {
            // given
            Long commentId = 1L;
            Comment comment = new Comment();
            Member member = new Member();
            CommentLike commentLike = new CommentLike();

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(securityUtil.getLoginMember()).thenReturn(member);
            when(commentLikeRepository.findByCommentAndMember(comment, member)).thenReturn(Optional.of(commentLike));

            // when
            commentLikeService.DeleteCommentLike(commentId);

            // then
            verify(commentLikeRepository, times(1)).delete(commentLike);
            verify(alarmService, times(1)).deleteCommentLikeAlarm(any(), any(), any(), any());
        }

        @Test
        @DisplayName("deleteCommentLike() 댓글 찾기 실패")
        void deleteCommentLikeCommentNotFound() {
            // given
            Long commentId = 1L;

            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

            // when, then
            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> commentLikeService.DeleteCommentLike(commentId))
                    .withMessage(COMMENT_NOT_FOUND.getMessage());

            verify(commentLikeRepository, never()).delete(any());
            verify(alarmService, never()).deleteCommentLikeAlarm(any(), any(), any(), any());
        }

        @Test
        @DisplayName("deleteCommentLike() CommentLike 찾기 실패")
        void deleteCommentLikeCommentLikeNotFound() {
            // given
            Long commentId = 1L;
            Comment comment = new Comment();
            Member member = new Member();

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(securityUtil.getLoginMember()).thenReturn(member);
            when(commentLikeRepository.findByCommentAndMember(comment, member)).thenReturn(Optional.empty());

            // when, then
            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> commentLikeService.DeleteCommentLike(commentId))
                    .withMessage(COMMENTLIKE_NOT_FOUND.getMessage());

            verify(commentLikeRepository, never()).delete(any());
            verify(alarmService, never()).deleteCommentLikeAlarm(any(), any(), any(), any());
        }
    }

    @Nested
    class GetCommentLikeUserPage {
        @Test
        @DisplayName("getCommentLikeUsers() 성공")
        void getCommentLikeUsersSuccess() {
            // given
            Long commentId = 1L;
            int page = 1;
            int size = 5;

            List<CommentLike> commentLikes = new ArrayList<>();

            Page<CommentLike> commentLikePage = new PageImpl<>(commentLikes);
            when(commentLikeRepository.findByCommentIdAndDeletedAtIsNull(commentId, PageRequest.of(page, size)))
                    .thenReturn(commentLikePage);

            // when
            PageListResponse<LikesMemberResponseDto> result = commentLikeService.getCommentLikeUsers(commentId, page, size);

            // then
            assertEquals(commentLikes.size(), result.getData().size());
            assertEquals(commentLikes.size(), result.getPageInfo().getTotalElements());
        }
    }

    // 동엽

    // 하늘

}