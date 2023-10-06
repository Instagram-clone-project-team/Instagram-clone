package com.project.Instagram.domain.post.controller;

import com.project.Instagram.domain.post.dto.PostResponse;
import com.project.Instagram.domain.post.service.PostService;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.domain.post.dto.PostCreateRequest;
import com.project.Instagram.domain.post.dto.EditPostRequest;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import static com.project.Instagram.global.response.ResultCode.POST_CREATE_SUCCESS;
import javax.validation.Valid;
  
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ResultResponse> createPost(@ModelAttribute PostCreateRequest postCreateRequest) throws IOException {
        postService.create(postCreateRequest);
        return ResponseEntity.ok(ResultResponse.of(POST_CREATE_SUCCESS));
    }

    @GetMapping("/allpost")
    public ResponseEntity<ResultResponse> getAllPostPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                 @Positive @RequestParam(value = "size", defaultValue = "5") int size) {
        PageListResponse<PostResponse> response = postService.getPostPageList(page - 1, size);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_POST_PAGE_SUCCESS,response));
    }
    @GetMapping("/{postId}")
    public ResponseEntity<ResultResponse> getPost(@PathVariable("postId") Long postId){
        final PostResponse postResponse =postService.getPostResponse(postId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_POST_SUCCESS,postResponse));
    }
    @GetMapping("/page/{memberId}")
    public ResponseEntity<ResultResponse> userGetAllPostPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                          @Positive @RequestParam(value = "size", defaultValue = "5") int size,
                                                          @PathVariable("memberId") Long memberId){
        PageListResponse<PostResponse> response = postService.getUserPostPage(memberId,page-1,size);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_POST_USER_PAGE_SUCCESS,response));
    }
    @GetMapping("/myposts")
    public ResponseEntity<ResultResponse> getMyPostPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                          @Positive @RequestParam(value = "size", defaultValue = "5") int size){
        PageListResponse<PostResponse> response = postService.getMyPostPage(page-1,size);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_POST_USER_PAGE_SUCCESS,response));
    }

    @PatchMapping("/update/content/{post_id}")
    public ResponseEntity<ResultResponse> editPost(@RequestBody @Valid EditPostRequest updatePostRequest, @PathVariable("post_id") Long postId) {
        postService.editPost(updatePostRequest, postId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.UPDATE_POST_SUCCESS));
    }
    @DeleteMapping("/delete/{post_id}")
    public ResponseEntity<ResultResponse> deletePost(@PathVariable("post_id") Long postId) {
        postService.delete(postId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_POST_SUCCESS));
    }
}
