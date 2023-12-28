package com.project.Instagram.domain.search.service;

import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.search.dto.HashTagResponseDto;
import com.project.Instagram.domain.search.dto.SearchDto;
import com.project.Instagram.domain.search.entity.RecentSearch;
import com.project.Instagram.domain.search.entity.Search;
import com.project.Instagram.domain.search.entity.SearchHashtag;
import com.project.Instagram.domain.search.entity.SearchMember;
import com.project.Instagram.domain.search.repository.RecentSearchRepository;
import com.project.Instagram.domain.search.repository.SearchHashtagRepository;
import com.project.Instagram.domain.search.repository.SearchMemberRepository;
import com.project.Instagram.domain.search.repository.SearchRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.project.Instagram.global.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {
    @InjectMocks
    SearchService searchService;
    @Mock
    SearchRepository searchRepository;
    @Mock
    SearchMemberRepository searchMemberRepository;
    @Mock
    SecurityUtil securityUtil;
    @Mock
    FollowRepository followRepository;
    @Mock
    RecentSearchRepository recentSearchRepository;
    @Mock
    SearchHashtagRepository searchHashtagRepository;

    // 윤영
    @Test
    @DisplayName("getRecommendMembersToFollow() 성공 테스트")
    void getRecommendMembersToFollow() {
        // given
        Member loginMember = new Member();
        loginMember.setId(1L);
        loginMember.setUsername("loginMember");

        Member member1 = new Member();
        member1.setId(2L);
        member1.setUsername("testMember1");

        Member member2 = new Member();
        member2.setId(3L);
        member2.setUsername("testMember2");

        List<SearchMember> recommendMembers = Arrays.asList(
                new SearchMember(member1),
                new SearchMember(member2));

        Member followMember1 = new Member();
        followMember1.setId(2L);
        followMember1.setUsername("testMember1");

        Member followMember2 = new Member();
        followMember1.setId(3L);
        followMember1.setUsername("testMember2");

        List<Follow> followList = Arrays.asList(
                new Follow(loginMember, followMember1),
                new Follow(loginMember, followMember2));

        when(searchMemberRepository.findAllByOrderByCountDesc()).thenReturn(recommendMembers);
        when(followRepository.findByMemberId(loginMember.getId())).thenReturn(followList);
        when(securityUtil.getLoginMember()).thenReturn(loginMember);

        // when
        List<Profile> result = searchService.getRecommendMembersToFollow();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("testMember1");
        assertThat(result.get(1).getUsername()).isEqualTo("testMember2");
        verify(searchMemberRepository, times(1)).findAllByOrderByCountDesc();
        verify(followRepository, times(1)).findByMemberId(loginMember.getId());
    }

    @Nested
    class AddRecentSearchAndUpCount {
        @Test
        @DisplayName("addRecentSearchAndUpCount() Member 타입 성공 테스트")
        void addRecentSearchAndUpCountForMemberSuccess() {
            // given
            Member loginMember = new Member();
            loginMember.setId(1L);
            loginMember.setUsername("loginMember");

            Member testMember = new Member();
            testMember.setId(2L);
            testMember.setUsername("testMember");

            when(securityUtil.getLoginMember()).thenReturn(loginMember);
            when(searchMemberRepository.findByMemberUsername("testMember")).thenReturn(Optional.of(new SearchMember(testMember)));

            // when
            searchService.addRecentSearchAndUpCount("Member", testMember.getUsername());

            // then
            verify(recentSearchRepository, times(1)).save(any(RecentSearch.class));
        }

        @Test
        @DisplayName("addRecentSearchAndUpCount() Hashtag 타입 성공 테스트")
        void addRecentSearchAndUpCountForHashtagSuccess(){
            // given
            Member loginMember = new Member();
            loginMember.setId(1L);
            loginMember.setUsername("loginMember");

            Hashtag hashtag = Hashtag.builder()
                    .tagName("#testHashtag")
                    .build();

            when(securityUtil.getLoginMember()).thenReturn(loginMember);
            when(searchHashtagRepository.findByHashtagTagName("testHashtag")).thenReturn(Optional.of(new SearchHashtag(hashtag)));

            // when
            searchService.addRecentSearchAndUpCount("Hashtag", hashtag.getTagName());

            // then
            verify(recentSearchRepository, times(1)).save(any(RecentSearch.class));
        }

        @Test
        @DisplayName("addRecentSearchAndUpCount() MEMBER_NOT_FOUND 테스트")
        void addRecentSearchAndUpCountForMemberNotFound() {
            // given
            Member loginMember = new Member();
            loginMember.setId(1L);
            loginMember.setUsername("loginMember");

            when(securityUtil.getLoginMember()).thenReturn(loginMember);
            when(searchMemberRepository.findByMemberUsername("nonExistentMember")).thenReturn(Optional.empty());

            // when, then
            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> searchService.addRecentSearchAndUpCount("Member", "nonExistentMember"))
                    .withMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("addRecentSearchAndUpCount() HASHTAG_MISMATCH 테스트")
        void addRecentSearchAndUpCountForHashtagMismatch() {
            // given
            Member loginMember = new Member();
            loginMember.setId(1L);
            loginMember.setUsername("loginMember");

            when(securityUtil.getLoginMember()).thenReturn(loginMember);

            // when, then
            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> searchService.addRecentSearchAndUpCount("Hashtag", "nonHashtagName"))
                    .withMessage(HASHTAG_MISMATCH.getMessage());
        }

        @Test
        @DisplayName("addRecentSearchAndUpCount() HASHTAG_NOT_FOUND 테스트")
        void addRecentSearchAndUpCountForHashtagNotFound() {
            // given
            Member loginMember = new Member();
            loginMember.setId(1L);
            loginMember.setUsername("loginMember");

            when(securityUtil.getLoginMember()).thenReturn(loginMember);
            when(searchHashtagRepository.findByHashtagTagName("nonexistentHashtag")).thenReturn(Optional.empty());

            // when, then
            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> searchService.addRecentSearchAndUpCount("Hashtag", "#nonexistentHashtag"))
                    .withMessage(HASHTAG_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("addRecentSearchAndUpCount() ENTITY_TYPE_INVALID 테스트")
        void addRecentSearchAndUpCountForInvalidEntityType() {
            // given
            Member loginMember = new Member();
            loginMember.setId(1L);
            loginMember.setUsername("loginMember");

            // when, then
            assertThrows(BusinessException.class, () -> searchService.addRecentSearchAndUpCount("InvalidType", "name"));

            assertThatExceptionOfType(BusinessException.class)
                    .isThrownBy(() -> searchService.addRecentSearchAndUpCount("InvalidType", "name"))
                    .withMessage(ENTITY_TYPE_INVALID.getMessage());
        }
    }

    @Test
    @DisplayName("deleteAllRecentSearch() 테스트")
    void deleteAllRecentSearch() {
        // given
        Member loginMember = new Member();
        loginMember.setId(1L);
        loginMember.setUsername("loginMember");

        when(securityUtil.getLoginMember()).thenReturn(loginMember);

        // when
        searchService.deleteAllRecentSearch();

        // then
        verify(recentSearchRepository, times(1)).deleteAllByMemberId(loginMember.getId());
    }


    // 동엽
    @Test
    @DisplayName("deleteRecentSearch Success")
    void deleteRecentSearch(){
        Member loginmember = new Member();
        loginmember.setId(1L);
        long searchId = 2L;

        when(securityUtil.getLoginMember()).thenReturn(loginmember);

        searchService.deleteRecentSearch(searchId);

        verify(recentSearchRepository,times(1)).deleteByMemberAndSearchId(loginmember,searchId);


    }
//    getTop15RecentSearches
    @Test
    @DisplayName("getAutoHashtag Success")
    void getAutoHashtagSuccess(){
        String text = "#사탕 ";

        List<Hashtag> hashtags = new ArrayList<>();
        Hashtag hashtag1 = Hashtag.builder()
                .tagName("사탕").build();
        Hashtag hashtag2 = Hashtag.builder()
                .tagName("사탕사랑").build();
        hashtags.add(hashtag1);
        hashtags.add(hashtag2);

        when(searchRepository.findHashTagsByText(text.substring(1))).thenReturn(hashtags);

        List<HashTagResponseDto> response =searchService.getAutoHashtag(text);

        assertEquals(2,response.size());
        assertEquals(hashtags.get(0).getTagName(),response.get(0).getName());
        assertEquals(hashtags.get(1).getTagName(),response.get(1).getName());

    }
    @Test
    @DisplayName("getAutoHashtag Fail(# 매치 안됨)")
    void getAutoHashtagFail(){
        String text = "사탕 ";

        assertThatExceptionOfType(BusinessException.class).isThrownBy(() ->searchService.getAutoHashtag(text))
                .withMessage(HASHTAG_MISMATCH.getMessage());
    }

    @Test
    @DisplayName("getTop15RecentSearches Success")
    void getTop15RecentSearches(){
        Member loginmember = new Member();
        loginmember.setId(1L);
        Member member1 = new Member();
        member1.setId(2L);
        Hashtag hashtag = Hashtag.builder().tagName("임임").build();
        List<Search> searches = new ArrayList<>();
        Search search1 = new SearchMember(member1);
        search1.setId(1L);
        search1.setDtype("MEMBER");
        Search search2 = new SearchHashtag(hashtag);
        search2.setId(2L);
        search2.setDtype("HASHTAG");

        searches.add(search1);
        searches.add(search2);

        int page =1;
        int size =15;
        Pageable pageable = PageRequest.of(page-1, size);
        when(securityUtil.getLoginMember()).thenReturn(loginmember);
        when(recentSearchRepository.findAllByMemberId(loginmember.getId(),pageable)).thenReturn(searches);

        Page<SearchDto> result = searchService.getTop15RecentSearches();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertEquals(15, result.getSize());
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(1);
        verify(searchRepository).findAllSearchMemberDtoByIdIn(any(),any());
        verify(searchRepository).findAllSearchHashtagDtoByIdIn(any());
    }


    // 하늘
}