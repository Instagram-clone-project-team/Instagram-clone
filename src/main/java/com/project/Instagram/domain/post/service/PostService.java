package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final SecurityUtil securityUtil;
    private final PostRepository postRepository;

    // 등록

    // 조회

    // 수정

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
