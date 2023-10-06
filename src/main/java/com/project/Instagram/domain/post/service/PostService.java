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
import com.project.Instagram.domain.post.dto.EditPostRequest;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void editPost(EditPostRequest editPostRequest, Long postId) {
        final Member loginMember = securityUtil.getLoginMember();
        final Post post = getPostWithMember(postId);

        if (!post.getMember().getId().equals(loginMember.getId())) throw new BusinessException(ErrorCode.POST_EDIT_FAILED);

        if(editPostRequest.getContent() != null) post.setContent(editPostRequest.getContent());
    }

    //삭제
    @Transactional
    public void delete(Long postId) {
        final Member loginMember = securityUtil.getLoginMember();
        final Post post = getPostWithMember(postId);

        if (!post.getMember().getId().equals(loginMember.getId())) throw new BusinessException(ErrorCode.POST_DELETE_FAILED);
        // deleted로 구분할 건지, 아니면 그냥 삭제할건지 토론
//        if (post.getDeletedAt() != null) throw new BusinessException(ErrorCode.POST_ALREADY_DELETED);
//        post.setDeletedAt(LocalDateTime.now());
        postRepository.delete(post);
    }

    private Post getPostWithMember(Long postId) {
        return postRepository.findWithMemberById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }
}
