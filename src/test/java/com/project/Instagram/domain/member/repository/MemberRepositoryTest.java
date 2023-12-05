package com.project.Instagram.domain.member.repository;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.global.config.QuerydslConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("[member repo] find by username")
    void test_find_by_username() {
        // given
        Member member = new Member();
        member.setId(1L);
        member.setUsername("luee");
        member.setName("haneul");
        member.setPassword("lueepwd1234");
        Member savedMember = memberRepository.save(member);

        // when
        Optional<Member> foundMember = memberRepository.findByUsername(member.getUsername());

        // then
        Assertions.assertEquals(true, foundMember.isPresent());
        Assertions.assertEquals(savedMember.getUsername(), foundMember.get().getUsername());
    }

    @Test
    @DisplayName("[member repo] find by username or email")
    void test_find_by_username_or_email() {
        // given
        Member member = new Member();
        member.setId(1L);
        member.setUsername("luee");
        member.setName("haneul");
        member.setPassword("lueepwd1234");
        member.setEmail("luee@gmail.com");
        Member savedMember = memberRepository.save(member);

        String wrongUsername = "wrongUsername";
        String wrongEmail = "wrongEmail@gmail.com";

        // when
        Member foundMemberByUsername = memberRepository.findByUsernameOrEmail(member.getUsername(), wrongEmail);
        Member foundMemberByEmail = memberRepository.findByUsernameOrEmail(wrongUsername, member.getEmail());
        Member foundMemberByWrongData = memberRepository.findByUsernameOrEmail(wrongUsername, wrongEmail);

        // then
        Assertions.assertEquals(savedMember.getUsername(), foundMemberByUsername.getUsername());
        Assertions.assertEquals(savedMember.getEmail(), foundMemberByUsername.getEmail());
        Assertions.assertEquals(savedMember.getUsername(), foundMemberByEmail.getUsername());
        Assertions.assertEquals(savedMember.getEmail(), foundMemberByEmail.getEmail());
        Assertions.assertEquals(null, foundMemberByWrongData);
    }

    @Test
    @DisplayName("[member repo] exists by username")
    void test_exists_by_username_success() {
        // given
        Member member = new Member();
        member.setId(1L);
        member.setUsername("luee");
        member.setName("haneul");
        member.setPassword("lueepwd1234");
        memberRepository.save(member);

        String wrongUsername = "wrongUsername";

        // when
        boolean isExistsByCorrectUsername = memberRepository.existsByUsername(member.getUsername());
        boolean isExistsByWrongUsername = memberRepository.existsByUsername(wrongUsername);

        // then
        Assertions.assertEquals(true, isExistsByCorrectUsername);
        Assertions.assertEquals(false, isExistsByWrongUsername);
    }

    @Test
    @DisplayName("[member repo] save")
    void test_member_save() {
        //given
        Member member = new Member();
        member.setUsername("luee");
        member.setName("haneul");
        member.setPassword("lueepwd1234");

        //when
        Member savedMember = memberRepository.save(member);
        List<Member> list=memberRepository.findAll();

        //when
        Assertions.assertEquals(member.getId(), savedMember.getId());
        Assertions.assertEquals(member.getUsername(), savedMember.getUsername());
    }

    @Nested
    class ExistsByEmail {
        @Test
        @DisplayName("이메일이 있는 경우 true 반환 테스트")
        void emailExistReturnTrue() {

            // given
            String email = "heo97@gmail";

            // when
            boolean existEmail = memberRepository.existsByEmail(email);

            // then
            assertThat(existEmail).isTrue();
        }

        @Test
        @DisplayName("이메일이 없는 경우 false 반환 테스트")
        void emailNotExistReturnFalse() {
            // given
            String email = RandomStringUtils.random(20, true, true);

            // when
            boolean notExistEmail = memberRepository.existsByEmail(email);

            // then
            assertThat(notExistEmail).isFalse();
        }
    }
}