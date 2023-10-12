package com.project.Instagram.domain.post.service;

import com.project.Instagram.domain.member.dto.LikesMemberResponseDto;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostLike;
import com.project.Instagram.domain.post.repository.PostLikeRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostRepository postRepository;
    private final SecurityUtil securityUtil;
    private final PostLikeRepository postLikeRepository;
    @Transactional
    public void postlike(Long postId) {
        final Post post = getPostbypostId(postId);
        final Member member = securityUtil.getLoginMember();

        if(postLikeRepository.findByMemberAndPost(member,post).isPresent()){
            throw new BusinessException(ErrorCode.POSTLIKE_ALREADY_EXIST);
        }
        PostLike postLike=new PostLike(member,post);
        postLikeRepository.save(postLike);
    }
    @Transactional
    public void postunlike(Long postId) {
        final Post post = getPostbypostId(postId);
        final Member member = securityUtil.getLoginMember();

        final PostLike postLike = postLikeRepository.findByMemberAndPost(member,post)
                .orElseThrow(()->new BusinessException(ErrorCode.POSTLIKE_NOT_FOUND));
        postLike.setDeletedAt(LocalDateTime.now());
    }
    @Transactional(readOnly = true)
    public PageListResponse<LikesMemberResponseDto> getPostLikeUsers(Long postId, int page, int size) {
        final Pageable pageable = PageRequest.of(page,size);
        final Member member = securityUtil.getLoginMember();

        Page<PostLike> postlikePage = postLikeRepository.findByPostId(postId,pageable);
        List<LikesMemberResponseDto> LikeproMemberResponseDtos = new ArrayList<>();
        postlikePage.forEach(postlike->{
            LikeproMemberResponseDtos.add(new LikesMemberResponseDto(postlike.getMember()));
        });
        return new PageListResponse<>(LikeproMemberResponseDtos,postlikePage);
    }

    private Post getPostbypostId(Long postId) {
        final Post post = postRepository.findWithMemberById(postId)
                .orElseThrow(()-> new BusinessException(ErrorCode.POST_NOT_FOUND));
        return post;
    }



}
