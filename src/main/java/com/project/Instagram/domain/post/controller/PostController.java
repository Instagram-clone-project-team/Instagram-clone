package com.project.Instagram.domain.post.controller;

import com.project.Instagram.domain.post.service.PostLikeService;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.project.Instagram.global.response.ResultCode.POST_LIKE_SUCCESS;
import static com.project.Instagram.global.response.ResultCode.POST_UNLIKE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostLikeService postLikeService;


    // 등록

    // 조회

    // 수정

    // 삭제

    //좋아요
    @PostMapping("/like")
    public ResponseEntity<ResultResponse> postLike(@RequestParam Long postId) {
        postLikeService.postlike(postId);
        return ResponseEntity.ok(ResultResponse.of(POST_LIKE_SUCCESS));
    }
    @DeleteMapping("/unlike")
    public ResponseEntity<ResultResponse> postUnlike(@RequestParam Long postId){
        postLikeService.postunlike(postId);
        return ResponseEntity.ok(ResultResponse.of(POST_UNLIKE_SUCCESS));
    }
}
