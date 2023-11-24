package com.project.Instagram.domain.search.service;

import com.project.Instagram.domain.follow.dto.FollowDto;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.search.dto.SearchDto;
import com.project.Instagram.domain.search.dto.SearchHashtagDto;
import com.project.Instagram.domain.search.dto.SearchMemberDto;
import com.project.Instagram.domain.search.entity.RecentSearch;
import com.project.Instagram.domain.search.entity.Search;
import com.project.Instagram.domain.search.entity.SearchHashtag;
import com.project.Instagram.domain.search.entity.SearchMember;
import com.project.Instagram.domain.search.repository.RecentSearchRepository;
import com.project.Instagram.domain.search.repository.SearchMemberRepository;
import com.project.Instagram.domain.search.repository.SearchRepository;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final SearchMemberRepository searchMemberRepository;

    // mewluee
    @Transactional
    public void deleteRecentSearch(long id) {
        Member loginMember = securityUtil.getLoginMember();
        recentSearchRepository.deleteByMemberAndSearchId(loginMember, id);
    }

    @Transactional(readOnly = true)
    public PageListResponse<SearchDto> getRecentSearchPageList(int page, int size) {
        Member loginMember = securityUtil.getLoginMember();
        Page<RecentSearch> pages = recentSearchRepository.findAllByMember(loginMember, PageRequest.of(page, size));
        List<RecentSearch> searches = pages.getContent();
        List<SearchDto> searchDtos = new ArrayList<>();

        for (RecentSearch rs : searches) {
            Search search_proxy = rs.getSearch();
            Search search = (Search) ((HibernateProxy) search_proxy)
                    .getHibernateLazyInitializer()
                    .getImplementation();

            switch (search_proxy.getDtype()) {
                case "MEMBER":
                    Member searchMember = ((SearchMember) search).getMember();
                    boolean isFollowing = followRepository.existsByMemberIdAndFollowMemberId(loginMember.getId(), searchMember.getId());
                    boolean isFollower = followRepository.existsByMemberIdAndFollowMemberId(searchMember.getId(), loginMember.getId());
                    SearchMemberDto searchMemberDto = new SearchMemberDto("MEMBER", searchMember, isFollowing, isFollower);
                    searchDtos.add(searchMemberDto);
                    break;
                case "HASHTAG":
                    Hashtag searchHashtag = ((SearchHashtag) search).getHashtag();
                    SearchHashtagDto searchHashtagDto = new SearchHashtagDto("HASHTAG", searchHashtag);
                    searchDtos.add(searchHashtagDto);
                    break;
                default:
                    break;
            }
        }
        return new PageListResponse(searchDtos, pages);
    }

    public List<Profile> getRecommendMembersToFollow() {
        //검색 카운트 내림차순으로 가져오기 - 이미 follow한 대상 -> 출력
        Member loginMember = securityUtil.getLoginMember();
        List<Member> recommendMemberList = searchMemberRepository.findAllByOrderByCountDesc().stream()
                .map(e -> e.getMember())
                .collect(Collectors.toList());
        List<Member> followMemberList = followRepository.findByMemberId(loginMember.getId()).stream()
                .map(e -> e.getFollowMember())
                .collect(Collectors.toList());
        List<Profile> deletedProfileList = recommendMemberList.stream()
                .filter(am -> followMemberList.stream().noneMatch(bm -> am == bm))
                .map(e -> Profile.convertMemberToProfile(e))
                .collect(Collectors.toList());
        return deletedProfileList;
    }
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
                .map(Search::getId)
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
