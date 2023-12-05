package com.project.Instagram.domain.comment.service;

import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.comment.dto.CommentResponse;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.repository.CommentRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.mention.service.MentionService;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.domain.post.service.HashtagService;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.project.Instagram.global.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    CommentService commentService;
    @Mock
    PostRepository postRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    SecurityUtil securityUtil;

    @Mock
    AlarmService alarmService;
    @Mock
    MentionService mentionService;
    @Mock
    HashtagService hashtagService;

    private final String DELETE_COMMENT = "삭제된 댓글입니다.";
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
    @Test
    @DisplayName("대댓글 작성 성공")
    void createReplyCommentTestSuccess(){
        Member member = new Member();
        Member targetmember = new Member();
        Post post = new Post();
        post.setId(1L);
        post.setDeletedAt(null);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setDeletedAt(null);
        String text = "testtest1212";

        when(securityUtil.getLoginMember()).thenReturn(member);
        when(postRepository.findByIdAndDeletedAtIsNull(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.existsByIdAndDeletedAtIsNull(post.getId())).thenReturn(true);
        when(commentRepository.existsByIdAndDeletedAtIsNull(post.getId())).thenReturn(true);
        commentService.createReplyComment(text,post.getId(), comment.getId());

        verify(commentRepository).save(any(Comment.class));
    }
    @Test
    @DisplayName("대댓글 작성 실패(게시글 삭제)")
    void createReplyCommentTestFail(){
        Member member = new Member();
        Post post = new Post();
        post.setId(1L);
        post.setDeletedAt(LocalDateTime.now());
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setDeletedAt(null);
        String text = "testtest1212";

        when(securityUtil.getLoginMember()).thenReturn(member);
        when(postRepository.findByIdAndDeletedAtIsNull(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.existsByIdAndDeletedAtIsNull(post.getId())).thenReturn(false);
        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() ->commentService.createReplyComment(text,post.getId(), comment.getId()))
                .withMessage(POST_NOT_FOUND.getMessage());
    }
    @Test
    @DisplayName("대댓글 작성 실패(댓글 삭제)")
    void createReplyComment(){
        Member member = new Member();
        Post post = new Post();
        post.setId(1L);
        post.setDeletedAt(null);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setDeletedAt(LocalDateTime.now());
        String text = "testtest1212";

        when(securityUtil.getLoginMember()).thenReturn(member);
        when(postRepository.findByIdAndDeletedAtIsNull(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.existsByIdAndDeletedAtIsNull(post.getId())).thenReturn(true);
        when(commentRepository.existsByIdAndDeletedAtIsNull(post.getId())).thenReturn(false);

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() ->commentService.createReplyComment(text,post.getId(), comment.getId()))
                .withMessage(COMMENT_NOT_FOUND.getMessage());
    }
    @Test
    @DisplayName("댓글 수정 성공")
    void updateCommenttestSuccess(){
        Member member = new Member();
        member.setId(3L);
        Post post = new Post();
        post.setId(2L);
        post.setMember(member);
        Comment comment = new Comment();
        comment.setId(1L);
        String beforetx = "이전 텍스트";
        comment.setText(beforetx);
        comment.setPostId(post.getId());
        comment.setWriter(member);
        String aftertext = "수정완료";

        when(securityUtil.getLoginMember()).thenReturn(member);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(postRepository.findByIdAndDeletedAtIsNull(comment.getPostId())).thenReturn(Optional.of(post));
        commentService.updateComment(comment.getId(),aftertext);

        verify(hashtagService).editHashTagOnComment(comment,beforetx);
        verify(mentionService).checkUpdateMentionsFromComment(member,beforetx,aftertext,post,comment);
    }
    @Test
    @DisplayName("댓글 수정 실패(댓글 작성자 매치 실패)")
    void updateCommenttest(){
        Member member = new Member();
        member.setId(3L);
        Member failmember = new Member();
        Post post = new Post();
        post.setId(2L);
        post.setMember(member);
        Comment comment = new Comment();
        comment.setId(1L);
        String beforetx = "이전 텍스트";
        comment.setText(beforetx);
        comment.setPostId(post.getId());
        comment.setWriter(failmember);
        String aftertext = "수정완료";

        when(securityUtil.getLoginMember()).thenReturn(member);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(postRepository.findByIdAndDeletedAtIsNull(comment.getPostId())).thenReturn(Optional.of(post));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() ->commentService.updateComment(comment.getId(),aftertext))
                .withMessage(COMMENT_WRITER_FAIL.getMessage());
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteCommentTestSuccess(){
        Member member = new Member();
        member.setId(3L);
        Post post = new Post();
        post.setId(2L);
        post.setMember(member);
        Comment comment = new Comment();
        comment.setId(1L);
        String beforetx = "이전 텍스트";
        comment.setText(beforetx);
        comment.setPostId(post.getId());
        comment.setWriter(member);

        when(securityUtil.getLoginMember()).thenReturn(member);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        commentService.deleteComment(comment.getId());

        assertNotNull(comment.getDeletedAt());
        assertEquals(comment.getText(),DELETE_COMMENT);
    }
    @Test
    @DisplayName("댓글 삭제 실패 (댓글 작성자 매치 실패)")
    void deleteCommentTestFail(){
        Member member = new Member();
        member.setId(3L);
        Member failmember = new Member();
        Post post = new Post();
        post.setId(2L);
        post.setMember(member);

        Comment comment = new Comment();
        comment.setId(1L);
        String beforetx = "이전 텍스트";
        comment.setText(beforetx);
        comment.setPostId(post.getId());
        comment.setWriter(failmember);

        when(securityUtil.getLoginMember()).thenReturn(member);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() ->commentService.deleteComment(comment.getId()))
                .withMessage(COMMENT_WRITER_FAIL.getMessage());
    }

    // 하늘

}