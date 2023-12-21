package com.project.Instagram.domain.search.repository;

import com.project.Instagram.config.TestConfig;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.post.repository.HashtagRepository;
import com.project.Instagram.domain.search.entity.RecentSearch;
import com.project.Instagram.domain.search.entity.Search;
import com.project.Instagram.domain.search.entity.SearchHashtag;
import com.project.Instagram.domain.search.entity.SearchMember;
import com.project.Instagram.global.config.QuerydslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({QuerydslConfig.class, TestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RecentSearchRepositoryTest {
    @Autowired
    RecentSearchRepository recentSearchRepository;
    @Autowired
    SearchMemberRepository searchMemberRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    HashtagRepository hashtagRepository;
    @Autowired
    SearchHashtagRepository searchHashtagRepository;
    // 윤영

    // 동엽
    @Test
    @DisplayName("findAllByMemberId")
    void findAllByMemberId(){
        Member loginmember = Member.builder()
                .username("exex22")
                .name("lele")
                .password("zzzqqq")
                .build();
        Member member1 = Member.builder()
                .username("exex4455")
                .name("2233ssxx")
                .password("zzzqqq")
                .build();
        memberRepository.save(loginmember);
        memberRepository.save(member1);
        SearchMember searchMember1 = searchMemberRepository.save(new SearchMember(member1));
        Hashtag hashtag1 = new Hashtag("사탕");
        hashtagRepository.save(hashtag1);
        SearchHashtag searchHashtag1 =searchHashtagRepository.save(new SearchHashtag(hashtag1));
        RecentSearch recentSearch1 =RecentSearch.builder()
                .member(loginmember)
                .search(searchMember1)
                .build();
        RecentSearch recentSearch2 =RecentSearch.builder()
                .member(loginmember)
                .search(searchHashtag1)
                .build();

        recentSearchRepository.save(recentSearch1);
        recentSearchRepository.save(recentSearch2);

        Pageable pageable = PageRequest.of(0, 15);

        // When
        List<Search> searchList= recentSearchRepository.findAllByMemberId(loginmember.getId(),pageable);

        assertThat(searchList).isNotNull();
        assertThat(searchList.get(0)).isEqualTo(searchMember1);
        assertThat(searchList.get(1)).isEqualTo(searchHashtag1);
        assertThat(searchList.size()).isEqualTo(2);
    }
    // 하늘
}