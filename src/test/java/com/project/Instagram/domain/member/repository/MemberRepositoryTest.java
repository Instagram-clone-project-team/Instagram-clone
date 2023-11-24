package com.project.Instagram.domain.member.repository;

import com.project.Instagram.config.TestConfig;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.RefreshToken;
import com.project.Instagram.global.config.QuerydslConfig;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.jwt.RefreshTokenRedisRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static com.project.Instagram.global.error.ErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@Import({QuerydslConfig.class, TestConfig.class})
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

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
        member.setId(1L);
        member.setUsername("luee");
        member.setName("haneul");
        member.setPassword("lueepwd1234");

        //when
        Member savedMember = memberRepository.save(member);

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
    @Nested
    class findByUsername{
        @Test
        @DisplayName("username이 존재 하는 경우 member 반환 테스트")
        void findByUsernameReturnTrue(){
            //given
            String username = "exex22";
            Member member = Member.builder()
                    .username(username)
                    .name("서서서")
                    .password("qwe123")
                    .build();

            Member member1 =memberRepository.save(member);
            // When
            Member findmember = memberRepository.findByUsername(username).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));

            // Then
            assertThat(findmember.getUsername()).isEqualTo(username);
            List<Member> members = memberRepository.findAll();
            assertThat(members.size()).isEqualTo(1);
        }
    }
    @Nested
    class findAllrefreshTokenByMemberId{
        @Test
        @DisplayName("해당 MemberId의 리프레쉬 토큰이 있는 경우")
        void findRefreshTokenByMemberId(){
            Member member = Member.builder()
                    .username("exex22")
                    .name("lelele")
                    .password("qwe123")
                    .build();
            member.setId(1L);
            memberRepository.save(member);
            RefreshToken refreshToken = RefreshToken.builder()
                    .memberId(member.getId())
                    .value("refreshToken")
                    .build();
            refreshTokenRedisRepository.save(refreshToken);

            List<RefreshToken> refreshTokens = refreshTokenRedisRepository.findAllByMemberId(member.getId());

            assertThat(refreshTokens.get(0).getMemberId()).isEqualTo(member.getId());
//왜 저장이 안될까?

        }
        @Test
        @DisplayName("해당 MemberId의 리프레쉬 토큰이 없는 경우")
        void findRefreshTokenByMemberIdFail(){
            Member member = Member.builder()
                    .username("exex22")
                    .name("lelele")
                    .password("qwe123")
                    .build();
            RefreshToken refreshToken = RefreshToken.builder()
                    .memberId(member.getId())
                    .value("refreshToken")
                    .build();
            memberRepository.save(member);
            refreshTokenRedisRepository.save(refreshToken);

            List<RefreshToken> refreshTokens = refreshTokenRedisRepository.findAllByMemberId(member.getId());
            assertThat(refreshTokens.size()).isEqualTo(0);
        }
    }
}