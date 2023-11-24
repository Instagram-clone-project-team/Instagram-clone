package com.project.Instagram.repository;

import com.project.Instagram.config.TestConfig;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.RefreshToken;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.config.QuerydslConfig;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.jwt.RefreshTokenRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.project.Instagram.global.error.ErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, TestConfig.class})
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

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
