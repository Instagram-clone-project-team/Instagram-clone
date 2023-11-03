package com.project.Instagram.domain.search.repository;

import com.project.Instagram.domain.search.dto.SearchHashtagDto;
import com.project.Instagram.domain.search.dto.SearchMemberDto;
import com.project.Instagram.domain.search.entity.Search;

import java.util.List;
import java.util.Map;

public interface SearchRepositoryQuerydsl {
    List<Search> findAllByTextLike(String text);

    void checkMatchingHashtag(String text, List<Search> searches, List<Long> searchIds);

    void checkMatchingMember(String text, List<Search> searches, List<Long> searchIds);

    Map<Long, SearchMemberDto> findAllSearchMemberDtoByIdIn(Long loginId, List<Long> searchIds);

    Map<Long, SearchHashtagDto> findAllSearchHashtagDtoByIdIn(List<Long> searchIds);
}
