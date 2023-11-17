package com.project.Instagram.domain.member.repository;

import com.project.Instagram.global.config.QuerydslConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

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