package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostLike;
import com.project.Instagram.domain.post.repository.PostLikeRepository;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostRepository postRepository;
    private final SecurityUtil securityUtil;
    private final PostLikeRepository postLikeRepository;

    public void postlike(Long postId) {
        final Post post = getPostbypostId(postId);
        final Member member = securityUtil.getLoginMember();

        if(postLikeRepository.findByMemberAndPost(member,post).isPresent()){
            throw new BusinessException(ErrorCode.POSTLIKE_ALREADY_EXIST);
        }
        PostLike postLike=new PostLike(member,post);
        postLikeRepository.save(postLike);
    }
    public void postunlike(Long postId) {
        final Post post = getPostbypostId(postId);
        final Member member = securityUtil.getLoginMember();

        final PostLike postLike = postLikeRepository.findByMemberAndPost(member,post)
                .orElseThrow(()->new BusinessException(ErrorCode.POSTLIKE_NOT_FOUND));
        postLikeRepository.delete(postLike);
    }

    private Post getPostbypostId(Long postId) {
        final Post post = postRepository.findWithMemberById(postId)
                .orElseThrow(()-> new BusinessException(ErrorCode.POST_NOT_FOUND));
        return post;
    }


}
