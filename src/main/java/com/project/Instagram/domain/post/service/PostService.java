package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.follow.service.FollowService;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.mention.service.MentionService;
import com.project.Instagram.domain.post.dto.PostResponse;
import com.project.Instagram.domain.post.dto.PostCreateRequest;
import com.project.Instagram.domain.post.dto.EditPostRequest;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.util.S3Uploader;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final SecurityUtil securityUtil;
    private final PostRepository postRepository;
    private final HashtagService hashtagService;
    private final S3Uploader s3Uploader;
    private final FollowService followService;
    private final MentionService mentionService;
    private static final String DIR_NAME = "story";

    public void create(PostCreateRequest postCreateRequest) throws IOException {
        Member member = securityUtil.getLoginMember();
        String str = s3Uploader.upload(postCreateRequest.getImage(), DIR_NAME);
        Post newPost = Post.builder()
                .member(member)
                .image(str)
                .content(postCreateRequest.getContent())
                .build();
        postRepository.save(newPost);
        hashtagService.registerHashtags(newPost);
        mentionService.checkMentionsFromPost(member, postCreateRequest.getContent(), newPost);
    }
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
        PostResponse postResponse = new PostResponse(post.getMember().getUsername(),post.getContent(),post.getImage());

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
            postResponses.add(new PostResponse(post.getMember().getUsername(),post.getContent(),post.getImage()));
        }
        PageListResponse<PostResponse> postResponsePage = new PageListResponse<>(postResponses, postPage);
        return postResponsePage;
    }

    @Transactional
    public void updatePost(EditPostRequest editPostRequest, Long postId) throws IOException {
        final Member loginMember = securityUtil.getLoginMember();
        final Post post = getPostWithMember(postId);
        String image =s3Uploader.upload(editPostRequest.getImage(), DIR_NAME);

        if (!post.getMember().getId().equals(loginMember.getId())) throw new BusinessException(ErrorCode.POST_EDIT_FAILED);
        String oldContent = post.getContent();
        post.updatePost(editPostRequest.getContent(), image);
        hashtagService.editHashTag(post,oldContent);
        mentionService.checkUpdateMentionsFromPost(loginMember, oldContent, editPostRequest.getContent(), post);
    }

    @Transactional
    public void delete(Long postId) {
        final Member loginMember = securityUtil.getLoginMember();
        final Post post = getPostWithMember(postId);

        if (!post.getMember().getId().equals(loginMember.getId())) throw new BusinessException(ErrorCode.POST_DELETE_FAILED);
        if (post.getDeletedAt() != null) throw new BusinessException(ErrorCode.POST_ALREADY_DELETED);

        post.setDeletedAt(LocalDateTime.now());
    }

    public Post getPostWithMember(Long postId) {
        return postRepository.findWithMemberById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }
      
    @Transactional
    public PageListResponse<PostResponse> getPostsByFollowedMembersPage(int page, int size) {
        final Long loginMemberId = securityUtil.getLoginMember().getId();
        List<Long> followedMemberIds = followService.getFollowedMemberIds(loginMemberId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByMemberIds(followedMemberIds, pageable);

        return getPostResponseListToPostResponsePage(posts);

    }
}
