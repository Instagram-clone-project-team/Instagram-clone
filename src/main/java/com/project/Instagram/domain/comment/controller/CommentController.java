package com.project.Instagram.domain.comment.controller;

import com.project.Instagram.domain.comment.dto.CommentRequest;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.service.CommentService;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    @PostMapping("comment")
    public ResponseEntity createComment(@RequestBody CommentRequest commentRequest){
        commentService.create(commentRequest.getText(), commentRequest.getPostId());
        return ResponseEntity.ok(ResultResponse.of(COMMENT_CREATE_SUCCESS));
    }

    @PostMapping("comment/reply")
    public ResponseEntity replyComment(@RequestBody CommentRequest commentRequest){
        commentService.reply(commentRequest.getText(), commentRequest.getPostId(), commentRequest.getParentsCommentId());
        return ResponseEntity.ok(ResultResponse.of(COMMENT_REPLY_SUCCESS));
    }

    @PatchMapping("comment/{comment-id}")
    public ResponseEntity updateComment(@RequestBody CommentRequest commentRequest,
                                        @PathVariable("comment-id") long commentId){
        commentService.update(commentId, commentRequest.getText());
        return ResponseEntity.ok(ResultResponse.of(COMMENT_UPDATE_SUCCESS));
    }

    @DeleteMapping("comment/{comment-id}")
    public ResponseEntity deleteComment(@PathVariable("comment-id") long commentId){
        commentService.delete(commentId);
        return ResponseEntity.ok(ResultResponse.of(COMMENT_DELETE_SUCCESS));
    }

    @GetMapping("comment/{post-id}")
    public ResponseEntity getComments(@PathVariable("post-id") long postId){
        List<Comment> list=commentService.get(postId);
        return ResponseEntity.ok(ResultResponse.of(COMMENT_GET_SUCCESS, list));
    }
}
