package com.project.Instagram.domain.post.controller;

import com.project.Instagram.domain.member.dto.LikesMemberResponseDto;
import com.project.Instagram.domain.post.service.PostLikeService;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.response.ResultCode;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

import static com.project.Instagram.global.response.ResultCode.*;
import static com.project.Instagram.global.response.ResultCode.POST_LIKE_SUCCESS;
import static com.project.Instagram.global.response.ResultCode.POST_UNLIKE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/postlike")
public class PostLikeController {
    private final PostLikeService postLikeService;
    @PostMapping // 동엽
    public ResponseEntity<ResultResponse> postLike(@RequestParam Long postId) {
        postLikeService.postlike(postId);
        return ResponseEntity.ok(ResultResponse.of(POST_LIKE_SUCCESS));
    }
    @DeleteMapping // 하늘
    public ResponseEntity<ResultResponse> postUnlike(@RequestParam Long postId){
        postLikeService.postunlike(postId);
        return ResponseEntity.ok(ResultResponse.of(POST_UNLIKE_SUCCESS));
    }
    @GetMapping("{postId}") // 윤영
    public ResponseEntity<ResultResponse> getThePostPostLikeUserPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                                    @Positive @RequestParam(value = "size", defaultValue = "5") int size,
                                                                    @PathVariable("postId") Long postId){
        PageListResponse<LikesMemberResponseDto> response = postLikeService.getPostLikeUsers(postId, page-1,size);
        return ResponseEntity.ok(ResultResponse.of(GET_POSTLIKE_USERS_SUCCESS,response));
    }
}
