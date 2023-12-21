package com.project.Instagram.domain.follow.repository;

import com.project.Instagram.config.TestConfig;
import com.project.Instagram.domain.follow.dto.FollowerDto;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.config.QuerydslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({QuerydslConfig.class, TestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FollowRepositoryQuerydslImplTest {

    @Autowired
    @Qualifier("followRepositoryQuerydslImpl")
    private FollowRepositoryQuerydsl followRepositoryQuerydsl;

    @Test
    @DisplayName("findFollowings() 테스트")
    void findFollowings() {
        // Given
        Long loginId = 1L;
        Long memberId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<FollowerDto> result = followRepositoryQuerydsl.findFollowings(loginId, memberId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
    }
}
