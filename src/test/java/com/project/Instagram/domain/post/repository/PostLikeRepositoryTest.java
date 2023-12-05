package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostLike;
import com.project.Instagram.global.config.QuerydslConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostLikeRepositoryTest {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;
    // 윤영

    // 동엽

    // 하늘
    @Test
    @DisplayName("find by member and post:success")
    void test_find_by_member_and_post() {
        // given
        Member member = new Member();
        member.setUsername("luee");
        member.setName("haneul");
        member.setPassword("lueepwd1234");
        member.setEmail("luee@gmail.com");
        memberRepository.save(member);

        Post post = Post.builder()
                .member(member)
                .image("image")
                .content("content")
                .build();
        postRepository.save(post);

        PostLike postLike = PostLike.builder()
                .member(member)
                .post(post)
                .build();
        postLikeRepository.save(postLike);

        // when
        Optional<PostLike> found = postLikeRepository.findByMemberAndPost(member, post);

        // then
        Assertions.assertEquals(postLike.getId(), found.get().getId());
    }
}