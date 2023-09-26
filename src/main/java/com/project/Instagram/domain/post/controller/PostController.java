package com.project.Instagram.domain.post.controller;

import com.project.Instagram.domain.post.dto.PostResponse;
import com.project.Instagram.domain.post.service.PostService;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.response.ResultCode;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;

@Validated
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    // 등록

    // 조회
    @GetMapping("/post/allpost")//게시물 목록 조회
    public ResponseEntity<ResultResponse> getAllPostPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                 @Positive @RequestParam(value = "size", defaultValue = "5") int size) {
        PageListResponse<PostResponse> response = postService.getPostPageList(page - 1, size);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_POST_PAGE_SUCCESS,response));
    }
    @GetMapping("/post/{postId}")
    public ResponseEntity<ResultResponse> getPost(@PathVariable("postId") Long postId){
        final PostResponse postResponse =postService.getPostResponse(postId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_POST_SUCCESS,postResponse));
    }
    @GetMapping("/post/page/{memberId}")
    public ResponseEntity<ResultResponse> userGetAllPostPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                          @Positive @RequestParam(value = "size", defaultValue = "5") int size,
                                                          @PathVariable("memberId") Long memberId){
        PageListResponse<PostResponse> response = postService.getUserPostPage(memberId,page-1,size);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_POST_USER_PAGE_SUCCESS,response));
    }
    @GetMapping("/post/myposts")
    public ResponseEntity<ResultResponse> getMyPostPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                          @Positive @RequestParam(value = "size", defaultValue = "5") int size){
        PageListResponse<PostResponse> response = postService.getMyPostPage(page-1,size);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_POST_USER_PAGE_SUCCESS,response));
    }
    // 수정

    // 삭제
}
