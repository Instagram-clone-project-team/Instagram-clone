package com.project.Instagram.domain.search.service;

import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.search.dto.HashTagResponseDto;
import com.project.Instagram.domain.search.dto.SearchDto;
import com.project.Instagram.domain.search.entity.Search;
import com.project.Instagram.domain.search.entity.SearchHashtag;
import com.project.Instagram.domain.search.entity.SearchMember;
import com.project.Instagram.domain.search.repository.RecentSearchRepository;
import com.project.Instagram.domain.search.repository.SearchRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
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

import static com.project.Instagram.global.error.ErrorCode.HASHTAG_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {
    @InjectMocks
    SearchService searchService;
    @Mock
    RecentSearchRepository recentSearchRepository;
    @Mock
    SecurityUtil securityUtil;
    @Mock
    SearchRepository searchRepository;
    @Mock
    FollowRepository followRepository;

    // 윤영

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