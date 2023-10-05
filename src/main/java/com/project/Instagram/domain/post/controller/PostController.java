package com.project.Instagram.domain.post.controller;

import com.project.Instagram.domain.post.dto.PostCreateRequest;
import com.project.Instagram.domain.post.service.PostService;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.project.Instagram.global.response.ResultCode.POST_CREATE_SUCCESS;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 등록
    @PostMapping("/post/create")
    public ResponseEntity<ResultResponse> createPost(@ModelAttribute PostCreateRequest postCreateRequest) throws IOException {
        postService.create(postCreateRequest);
        return ResponseEntity.ok(ResultResponse.of(POST_CREATE_SUCCESS));
    }

    // 조회

    // 수정

    // 삭제
}
