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

import static com.project.Instagram.global.response.ResultCode.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    @PostMapping // 동엽
    public ResponseEntity<ResultResponse> createPost(@ModelAttribute PostCreateRequest postCreateRequest) throws IOException {
        postService.create(postCreateRequest);
        return ResponseEntity.ok(ResultResponse.of(POST_CREATE_SUCCESS));
    }

    @GetMapping("/allpost") // 하늘
    public ResponseEntity<ResultResponse> getAllPostPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                 @Positive @RequestParam(value = "size", defaultValue = "5") int size) {
        PageListResponse<PostResponse> response = postService.getPostPageList(page - 1, size);
        return ResponseEntity.ok(ResultResponse.of(GET_POST_PAGE_SUCCESS,response));
    }
    @GetMapping("/{postId}") // 윤영
    public ResponseEntity<ResultResponse> getPost(@PathVariable("postId") Long postId){
        final PostResponse postResponse =postService.getPostResponse(postId);
        return ResponseEntity.ok(ResultResponse.of(GET_POST_SUCCESS,postResponse));
    }
    @GetMapping("/page/{memberId}") // 윤영
    public ResponseEntity<ResultResponse> userGetAllPostPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                          @Positive @RequestParam(value = "size", defaultValue = "5") int size,
                                                          @PathVariable("memberId") Long memberId){
        PageListResponse<PostResponse> response = postService.getUserPostPage(memberId,page-1,size);
        return ResponseEntity.ok(ResultResponse.of(GET_POST_USER_PAGE_SUCCESS,response));
    }
    @GetMapping("/myposts") // 하늘
    public ResponseEntity<ResultResponse> getMyPostPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                          @Positive @RequestParam(value = "size", defaultValue = "5") int size){
        PageListResponse<PostResponse> response = postService.getMyPostPage(page-1,size);
        return ResponseEntity.ok(ResultResponse.of(GET_POST_USER_PAGE_SUCCESS,response));
    }

    @PatchMapping("{post_id}") // 동엽
    public ResponseEntity<ResultResponse> updatePost(@ModelAttribute EditPostRequest updatePostRequest, @PathVariable("post_id") Long postId) throws IOException {
        postService.updatePost(updatePostRequest, postId);
        return ResponseEntity.ok(ResultResponse.of(UPDATE_POST_SUCCESS));
    }
    @DeleteMapping("{post_id}") // 하늘
    public ResponseEntity<ResultResponse> deletePost(@PathVariable("post_id") Long postId) {
        postService.delete(postId);
        return ResponseEntity.ok(ResultResponse.of(DELETE_POST_SUCCESS));
    }

    @GetMapping("/followed-posts") // 동엽
    public ResponseEntity<ResultResponse> getFollowedPostsPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                               @Positive @RequestParam(value = "size", defaultValue = "5") int size) {
        PageListResponse<PostResponse> response = postService.getPostsByFollowedMembersPage(page - 1, size);
        return ResponseEntity.ok(ResultResponse.of(GET_FOLLOWED_POSTS_SUCCESS, response));
    }
}
