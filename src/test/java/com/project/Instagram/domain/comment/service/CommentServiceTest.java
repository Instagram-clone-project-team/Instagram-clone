package com.project.Instagram.domain.comment.service;

import com.project.Instagram.domain.comment.dto.CommentResponse;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.repository.CommentRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.project.Instagram.global.error.ErrorCode.POST_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    CommentService commentService;
    @Mock
    PostRepository postRepository;
    @Mock
    CommentRepository commentRepository;

    // 윤영
    @Nested
    class GetCommentsByPostId {
        @Test
        @DisplayName("getCommentsByPostId() 성공 테스트")
        void getCommentsByPostIdSuccess() {
            // given
            Long postId = 1L;

            Member member = Member.builder()
                    .username("testUsername")
                    .email("test122@gmail.com")
                    .build();

            Comment parentComment = Comment.builder()
                    .writer(member)
                    .postId(postId)
                    .text("ParentComment")
                    .build();
            parentComment.setId(1L);

            Comment childComment1 = Comment.builder()
                    .writer(member)
                    .postId(postId)
                    .text("ChildComment1")
                    .parentsCommentId(parentComment.getId())
                    .replyOrder(1)
                    .build();
            childComment1.setId(2L);

            Comment childComment2 = Comment.builder()
                    .writer(member)
                    .postId(postId)
                    .text("ChildComment2")
                    .parentsCommentId(parentComment.getId())
                    .replyOrder(2)
                    .build();
            childComment2.setId(3L);

            List<Comment> comments = List.of(parentComment, childComment1, childComment2);

            when(postRepository.existsByIdAndDeletedAtIsNull(postId)).thenReturn(true);
            when(commentRepository.findAllByPostId(postId)).thenReturn(comments);

            // when
            List<CommentResponse> commentResponses = commentService.getCommentsByPostId(postId);

            // then
            verify(postRepository, times(1)).existsByIdAndDeletedAtIsNull(postId);
            verify(commentRepository, times(1)).findAllByPostId(postId);
            assertEquals(1, commentResponses.size());
            assertEquals(parentComment.getText(), commentResponses.get(0).getComment().getText());
            assertEquals(2, commentResponses.get(0).getReplies().size());
            assertEquals(childComment2.getText(), commentResponses.get(0).getReplies().get(0).getText());
        }

        @Test
        @DisplayName("getCommentsByPostId() 예외 테스트")
        void GetCommentsByPostIdPostNotFound() {
            // given
            Long postId = 1L;
            when(postRepository.existsByIdAndDeletedAtIsNull(postId)).thenReturn(false);

            // when, then
            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> commentService.getCommentsByPostId(postId))
                    .withMessage(POST_NOT_FOUND.getMessage());

            verify(commentRepository, never()).findAllByPostId(postId);
        }

    }

    // 동엽

    // 하늘

}