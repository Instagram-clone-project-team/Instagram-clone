package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.dto.PostCreateRequest;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.util.S3Uploader;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final S3Uploader s3Uploader;
    private final PostRepository postRepository;
    private final SecurityUtil securityUtil;
    private static final String DIR_NAME = "story";

    // 등록
    public void create(PostCreateRequest postCreateRequest) throws IOException {
        Member member = securityUtil.getLoginMember();
        String str = s3Uploader.upload(postCreateRequest.getImage(), DIR_NAME);
        Post newPost = Post.builder()
                .member(member)
                .image(str)
                .content(postCreateRequest.getContent())
                .build();
        postRepository.save(newPost);
    }
    // 조회

    // 수정

    //삭제

}
