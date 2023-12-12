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

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({QuerydslConfig.class, TestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FollowRepositoryTest {
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("count active followers by member username")
    void test_count_active_Followers_By_Member_Username(){
        //given
        int count=5;
        Member member=Member.builder()
                .username("luee")
                .name("name")
                .email("email@gmail.com")
                .password("lueepassword")
                .build();
        memberRepository.save(member);
        for(int i=0; i<count; i++){
            Member follower=Member.builder()
                    .username("luee"+i)
                    .name("name"+i)
                    .email("email"+i+"@gmail.com")
                    .password("lueepassword"+i)
                    .build();
            memberRepository.save(follower);
            followRepository.save(new Follow(follower, member));
        }
        //when
        int response=followRepository.countActiveFollowersByMemberUsername(member.getUsername());
        //then
        assertEquals(response, count);
    }
}