package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.global.config.QuerydslConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    // 윤영

    // 동엽

    // 하늘
    @Test
    @DisplayName("find all post page:success")
    void test_find_all_post_page() {
        // given
        Member member = new Member();
        member.setUsername("luee");
        member.setName("haneul");
        member.setPassword("lueepwd1234");
        member.setEmail("luee@gmail.com");
        memberRepository.save(member);

        int count = 30;
        for (int i = 1; i <= count; i++) {
            Post post = Post.builder()
                    .member(member)
                    .image("image" + i)
                    .content("content" + i)
                    .build();
            postRepository.save(post);
        }

        Pageable pageable = PageRequest.of(1, 5);

        // when
        Page<Post> posts = postRepository.findAllPostPage(pageable);

        // then
        Assertions.assertEquals(count, posts.getTotalElements());
    }

    @Test
    @DisplayName("find member all post page:success")
    void test_find_member_all_post_page() {
        // given
        Member member = new Member();
        member.setUsername("luee");
        member.setName("haneul");
        member.setPassword("lueepwd1234");
        member.setEmail("luee@gmail.com");
        memberRepository.save(member);

        Member member2 = new Member();
        member2.setUsername("luee2");
        member2.setName("haneul2");
        member2.setPassword("lueepwd12342");
        member2.setEmail("luee2@gmail.com");
        Member savedMember2 = memberRepository.save(member2);

        int count = 20;
        for (int i = 1; i <= count; i++) {
            Post post = Post.builder()
                    .member(member)
                    .image("image" + i)
                    .content("content" + i)
                    .build();
            postRepository.save(post);
        }
        int count2 = 10;
        for (int i = 1; i <= count2; i++) {
            Post post = Post.builder()
                    .member(member2)
                    .image("image" + i)
                    .content("content" + i)
                    .build();
            postRepository.save(post);
        }

        Pageable pageable = PageRequest.of(1, 5);

        // when
        Page<Post> posts = postRepository.findMemberAllPostPage(savedMember2.getId(), pageable);

        // then
        Assertions.assertEquals(count2, posts.getTotalElements());
    }

}