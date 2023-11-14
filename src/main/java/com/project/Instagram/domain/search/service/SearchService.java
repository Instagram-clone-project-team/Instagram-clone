package com.project.Instagram.domain.search.service;

import com.project.Instagram.domain.follow.dto.FollowDto;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.search.dto.SearchDto;
import com.project.Instagram.domain.search.dto.SearchHashtagDto;
import com.project.Instagram.domain.search.dto.SearchMemberDto;
import com.project.Instagram.domain.search.entity.Search;
import com.project.Instagram.domain.search.repository.RecentSearchRepository;
import com.project.Instagram.domain.search.repository.SearchRepository;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SecurityUtil securityUtil;
    private final SearchRepository searchRepository;
    private final FollowRepository followRepository;
    private final RecentSearchRepository recentSearchRepository;

    // mewluee

    // DongYeopMe

    // Heo-y-y
    @Transactional(readOnly = true)
    public List<SearchDto> searchByText(String text) {
        String keyword = text.trim();
        final Long loginId = securityUtil.getLoginMember().getId();
        List<Search> searches;

        if (keyword.charAt(0) == '#') {
            if (keyword.equals("#")) {
                return Collections.emptyList();
            }
            searches = searchRepository.findHashtagsByTextLike(keyword.substring(1));
        } else {
            searches = searchRepository.findAllByTextLike(keyword);
        }

        final List<Long> searchIds = searches.stream()
                .map(Search :: getId)
                .collect(Collectors.toList());

        searchRepository.checkMatchingHashtag(keyword.substring(1), searches, searchIds);
        searchRepository.checkMatchingMember(keyword, searches, searchIds);

        return setSearchContent(loginId, searches, searchIds);
    }

    @Transactional(readOnly = true)
    public Page<SearchDto> getTop15RecentSearches() {
        final Long loginId = securityUtil.getLoginMember().getId();
        final Pageable pageable = PageRequest.of(0, 15);
        final List<Search> searches = recentSearchRepository.findAllByMemberId(loginId, pageable);

        final List<Long> searchIds = searches.stream()
                .map(Search::getId)
                .collect(Collectors.toList());

        final List<SearchDto> searchDtos = setSearchContent(loginId, searches, searchIds);
        final Long total = recentSearchRepository.countAllByMemberId(loginId);

        return new PageImpl<>(searchDtos, pageable, total);
    }

    @Transactional
    public void deleteAllRecentSearch() {
        final Long loginId = securityUtil.getLoginMember().getId();
        recentSearchRepository.deleteAllByMemberId(loginId);
    }


    private List<SearchDto> setSearchContent(Long loginId, List<Search> searches, List<Long> searchIds) {
        final Map<Long, SearchMemberDto> memberMap = searchRepository.findAllSearchMemberDtoByIdIn(loginId, searchIds);
        final Map<Long, SearchHashtagDto> hashtagMap = searchRepository.findAllSearchHashtagDtoByIdIn(searchIds);
        final List<String> searchUsernames = memberMap.values().stream().map(s -> s.getMember().getUsername()).collect(Collectors.toList());
        final Map<String, List<FollowDto>> followsMap = followRepository.findFollowingMemberFollowMap(loginId, searchUsernames);

        memberMap.forEach(
                (id, member) -> member.setFollowingMemberFollow(
                        followsMap.get(member.getMember().getUsername()),
                        3));

        return searches.stream()
                .map(search -> {
                    switch (search.getDtype()) {
                        case "MEMBER":
                            return memberMap.get(search.getId());
                        case "HASHTAG":
                            return hashtagMap.get(search.getId());
                        default:
                            return null;
                    }
                })
                .collect(Collectors.toList());

    }
}