package com.project.Instagram.domain.follow.repository;

import com.project.Instagram.config.TestConfig;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.config.QuerydslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, TestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FollowRepositoryTest {
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    MemberRepository memberRepository;
    @Test
    @DisplayName("countActiveFollowsByMemberUsername 테스트")
    @Transactional
    void countActiveFollowsByMemberUsername(){
        String username = "exex4455";
        Member loginmember = Member.builder()
                .username("exex22")
                .name("lele")
                .password("zzzqqq")
                .build();
        loginmember.setId(1L);
        Member followmember = Member.builder()
                .username(username)
                .name("2233ssxx")
                .password("zzzqqq")
                .build();
        followmember.setId(2L);
        memberRepository.save(loginmember);
        memberRepository.save(followmember);

        Follow follow = new Follow(loginmember,followmember);
        loginmember.increaseFollowerCount();
        followRepository.save(follow);
        int result = followRepository.countActiveFollowsByMemberUsername("exex22");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1);

    }
}