package com.project.Instagram.domain.comment.controller;

import com.project.Instagram.domain.comment.service.CommentLikeService;
import com.project.Instagram.domain.member.dto.LikesMemberResponseDto;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

import static com.project.Instagram.global.response.ResultCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/commentlike")
public class CommentLikeController {

    final private CommentLikeService commentLikeService;

    @PostMapping
    public ResponseEntity<ResultResponse> commentLike(@RequestParam Long commentId) {
        commentLikeService.createCommentLike(commentId);
        return ResponseEntity.ok(ResultResponse.of(COMMENT_LIKE_SUCCESS));
    }
    @DeleteMapping
    public ResponseEntity<ResultResponse> commentUnLike(@RequestParam Long commentId){
        commentLikeService.DeleteCommentLike(commentId);
        return ResponseEntity.ok(ResultResponse.of(COMMENT_UNLIKE_SUCCESS));
    }
    @GetMapping("{commentId}")
    public ResponseEntity<ResultResponse> GetCommentLikeUserPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                                     @Positive @RequestParam(value = "size", defaultValue = "5") int size,
                                                                     @PathVariable("commentId") Long commentId){
        PageListResponse<LikesMemberResponseDto> response = commentLikeService.getCommentLikeUsers(commentId, page-1,size);
        return ResponseEntity.ok(ResultResponse.of(GET_POSTLIKE_USERS_SUCCESS,response));
    }



}
