package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.dto.PostResponse;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final SecurityUtil securityUtil;
    private final PostRepository postRepository;


    // 등록

    // 조회
    public PageListResponse<PostResponse> getPostPageList(int page, int size) {
        securityUtil.checkLoginMember();
        final Pageable pageable = PageRequest.of(page,size);
        final Page<Post> postPage =postRepository.findAllPostPage(pageable);
        PageListResponse<PostResponse> postResponsePage = getPostResponseListToPostResponsePage(postPage);
        return postResponsePage;
    }



    public PostResponse getPostResponse(Long postId) {
        securityUtil.checkLoginMember();
        final Post post = postRepository.findById(postId)
                .orElseThrow(() ->new BusinessException(ErrorCode.POST_NOT_FOUND));
        PostResponse postResponse = new PostResponse(post.getMember().getUsername(),post.getContent());

        return postResponse;
    }


    public PageListResponse<PostResponse> getUserPostPage(Long memberId,int page,int size) {
        final Pageable pageable = PageRequest.of(page,size);
        final Page<Post> postPage= postRepository.findMemberAllPostPage(memberId,pageable);
        PageListResponse<PostResponse> response = getPostResponseListToPostResponsePage(postPage);
        return response;
    }
    public PageListResponse<PostResponse> getMyPostPage(int page,int size){
        final Member member = securityUtil.getLoginMember();
        final Pageable pageable = PageRequest.of(page,size);
        final Page<Post> postPage= postRepository.findMemberAllPostPage(member.getId(), pageable);
        PageListResponse<PostResponse> response = getPostResponseListToPostResponsePage(postPage);
        return response;
    }

    private  PageListResponse<PostResponse> getPostResponseListToPostResponsePage(Page<Post> postPage) {
        List<Post> posts = postPage.getContent();
        List<PostResponse> postResponses =  new ArrayList<>();
        for(Post post : posts){
            postResponses.add(new PostResponse(post.getMember().getUsername(),post.getContent()));
        }
        PageListResponse<PostResponse> postResponsePage = new PageListResponse<>(postResponses, postPage);
        return postResponsePage;
    }


    // 수정

    //삭제

}
