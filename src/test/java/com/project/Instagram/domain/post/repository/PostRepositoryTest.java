package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.global.config.QuerydslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    // 윤영
    @Test
    @DisplayName("findMemberAllPostPage() 성공")
    void findMemberAllPostPageTrue() {
        // given
        Member member = new Member();
        member.setId(1L);
        member.setUsername("testMember");

        int page = 0;
        int size = 5;
        PageRequest pageRequest = PageRequest.of(page, size);

        // when
        Page<Post> resultPage = postRepository.findMemberAllPostPage(2L, pageRequest);

        // then
        assertTrue(resultPage.getTotalElements() > 0);
        assertEquals(1, resultPage.getTotalElements());
    }

    // 동엽

    // 하늘

}