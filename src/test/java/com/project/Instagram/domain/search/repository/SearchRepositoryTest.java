package com.project.Instagram.domain.search.repository;

import com.project.Instagram.config.TestConfig;
import com.project.Instagram.domain.follow.dto.FollowerDto;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.post.repository.HashtagRepository;
import com.project.Instagram.domain.search.dto.SearchHashtagDto;
import com.project.Instagram.domain.search.dto.SearchMemberDto;
import com.project.Instagram.domain.search.entity.SearchHashtag;
import com.project.Instagram.domain.search.entity.SearchMember;
import com.project.Instagram.global.config.QuerydslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({QuerydslConfig.class, TestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SearchRepositoryTest {
    @Autowired
    SearchRepository searchRepository;
    @Autowired
    HashtagRepository hashtagRepository;
    @Autowired
    SearchHashtagRepository searchHashtagRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FollowRepository followRepository;
    @Autowired
    SearchMemberRepository searchMemberRepository;

    //follow: findFollowingMemberFollowMap
    // 윤영

    // 동엽
    @Test
    @DisplayName("findHashTagsByText By textName")
    void findHashTagsByText(){
        Hashtag hashtag1 = new Hashtag("사탕");
        Hashtag hashtag2 = new Hashtag("사탕맛수박");
        hashtagRepository.save(hashtag1);
        searchHashtagRepository.save(new SearchHashtag(hashtag1));
        hashtagRepository.save(hashtag2);
        searchHashtagRepository.save(new SearchHashtag(hashtag2));

        List<Hashtag> result = searchRepository.findHashTagsByText("사탕");

        assertThat(result).isNotNull();
        assertThat(result.get(0)).isEqualTo(hashtag1);
        assertThat(result.get(1)).isEqualTo(hashtag2);
        assertThat(result.size()).isEqualTo(2);
    }
    @Test
    @DisplayName("findAllSearchHashtagDtoByIdIn")
    void findAllSearchHashtagDtoByIdIn(){
        Hashtag hashtag1 = new Hashtag("사탕");
        Hashtag hashtag2 = new Hashtag("사탕맛수박");
        hashtagRepository.save(hashtag1);
        SearchHashtag searchHashtag1 =searchHashtagRepository.save(new SearchHashtag(hashtag1));
        hashtagRepository.save(hashtag2);
        SearchHashtag searchHashtag2 =searchHashtagRepository.save(new SearchHashtag(hashtag2));

        Map<Long, SearchHashtagDto> result = searchRepository.findAllSearchHashtagDtoByIdIn(List.of(searchHashtag1.getId(),searchHashtag2.getId()));

        assertThat(result).isNotNull();
        assertThat(result).containsKey(searchHashtag1.getId());
        assertThat(result.get(searchHashtag1.getId()).getName()).isEqualTo(searchHashtag1.getHashtag().getTagName());
        assertThat(result).containsKey(searchHashtag2.getId());
        assertThat(result.get(searchHashtag2.getId()).getName()).isEqualTo(searchHashtag2.getHashtag().getTagName());
        assertThat(result).hasSize(2);
    }
    @Test
    @DisplayName("findAllSearchMemberDtoByIdIn")
    void findAllSearchMemberDtoByIdIn(){
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
        Member login = memberRepository.save(loginmember);
        memberRepository.save(member2);
        memberRepository.save(member3);
        SearchMember searchMember2 = searchMemberRepository.save(new SearchMember(member2));
        SearchMember searchMember3 = searchMemberRepository.save(new SearchMember(member3));


        Follow follow = new Follow(loginmember, member2);
        loginmember.increaseFollowerCount();
        followRepository.save(follow);

        Map<Long, SearchMemberDto> result = searchRepository.findAllSearchMemberDtoByIdIn(login.getId(),List.of(searchMember2.getId(),searchMember3.getId()));

        assertThat(result).isNotNull();
        assertThat(result).containsKey(searchMember2.getId());
        assertThat(result.get(searchMember2.getId()).getMember().getUsername()).isEqualTo(member2.getUsername());
        assertThat(result).containsKey(searchMember3.getId());
        assertThat(result.get(searchMember3.getId()).getMember().getUsername()).isEqualTo(member3.getUsername());
        assertThat(result).hasSize(2);
    }
    // 하늘

}