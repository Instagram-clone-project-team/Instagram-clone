package com.project.Instagram.domain.follow.repository;

import com.project.Instagram.config.TestConfig;
import com.project.Instagram.domain.follow.dto.FollowDto;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({QuerydslConfig.class, TestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FollowRepositoryTest {
    @Autowired
    FollowRepository followRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("countActiveFollowsByMemberUsername 테스트")
    @Transactional
    void countActiveFollowsByMemberUsername() {
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

        Follow follow = new Follow(loginmember, followmember);
        loginmember.increaseFollowerCount();
        followRepository.save(follow);
        int result = followRepository.countActiveFollowsByMemberUsername("exex22");
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1);

    }

    @Test
    @DisplayName("count active followers by member username")
    void test_count_active_Followers_By_Member_Username() {
        //given
        int count = 5;
        Member member = Member.builder()
                .username("luee")
                .name("name")
                .email("email@gmail.com")
                .password("lueepassword")
                .build();
        memberRepository.save(member);
        for (int i = 0; i < count; i++) {
            Member follower = Member.builder()
                    .username("luee" + i)
                    .name("name" + i)
                    .email("email" + i + "@gmail.com")
                    .password("lueepassword" + i)
                    .build();
            memberRepository.save(follower);
            followRepository.save(new Follow(follower, member));
        }
        //when
        int response = followRepository.countActiveFollowersByMemberUsername(member.getUsername());
        //then
        assertEquals(response, count);
    }
    @Test
    @DisplayName("findFollowingMemberFollowMap")
    void findFollowingMemberFollowMap(){
        Member loginmember = Member.builder()
                .username("exex22")
                .name("lele")
                .password("zzzqqq")
                .build();

        Member member3 = Member.builder()
                .username("exex4455")
                .name("2233ssxx")
                .password("zzzqqq")
                .build();

        Member member2 = Member.builder()
                .username("sssxxxzzz")
                .name("244464dd")
                .password("zzzqqq")
                .build();

        memberRepository.save(loginmember);
        memberRepository.save(member2);
        memberRepository.save(member3);

        Follow follow1 = new Follow(loginmember, member2);
        loginmember.increaseFollowerCount();
        followRepository.save(follow1);
        Follow follow2 = new Follow(loginmember, member3);
        loginmember.increaseFollowerCount();
        followRepository.save(follow2);
        Follow follow3 = new Follow(member2, loginmember);
        member2.increaseFollowerCount();
        followRepository.save(follow3);
        Map<String, List<FollowDto>> result = followRepository.findFollowingMemberFollowMap(loginmember.getId(), List.of(member2.getUsername(),member3.getUsername()));
        System.out.println(result);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsKey(member2.getUsername());
        assertThat(result).containsKey(member3.getUsername());
        assertThat(result.get(member2.getUsername()).get(0).getMemberUsername()).isEqualTo(follow1.getMember().getUsername());
        assertThat(result.get(member2.getUsername()).get(0).getFollowMemberUsername()).isEqualTo(follow1.getFollowMember().getUsername());
        assertThat(result.get(member3.getUsername()).get(0).getMemberUsername()).isEqualTo(follow2.getMember().getUsername());
        assertThat(result.get(member3.getUsername()).get(0).getFollowMemberUsername()).isEqualTo(follow2.getFollowMember().getUsername());

    }
}