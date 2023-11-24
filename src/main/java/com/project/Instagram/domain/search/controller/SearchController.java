package com.project.Instagram.domain.search.controller;

import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.search.dto.SearchDto;
import com.project.Instagram.domain.search.service.SearchService;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    // mewluee
    @DeleteMapping("/{search-id}")
    public ResponseEntity<ResultResponse> deleteRecentSearch(@PathVariable("search-id") long id) {
        searchService.deleteRecentSearch(id);
        return ResponseEntity.ok(ResultResponse.of(DELETE_RECENT_SEARCH_SUCCESS));
    }

    @GetMapping("recent/all")
    public ResponseEntity<ResultResponse> getAllRecentSearchPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                          @Positive @RequestParam(value = "size", defaultValue = "5") int size) {
        PageListResponse<SearchDto> response = searchService.getRecentSearchPageList(page - 1, size);
        return ResponseEntity.ok(ResultResponse.of(GET_RECENTSEARCH_LIST_SUCCESS, response));
    }

    @GetMapping("/recommend-member")
    public ResponseEntity<ResultResponse> getRecommendMembersToFollow(){
        List<Profile> response = searchService.getRecommendMembersToFollow();
        return ResponseEntity.ok(ResultResponse.of(GET_RECOMMEND_MEMBER_LIST_SUCCESS, response));
    }
    // DongYeopMe

    // Heo-y-y
    @GetMapping
    public ResponseEntity<ResultResponse> search(@RequestParam String text) {
        final List<SearchDto> searchDtos = searchService.searchByText(text);
        return ResponseEntity.ok(ResultResponse.of(SEARCH_SUCCESS, searchDtos));
    }

    @GetMapping("/recent/top")
    public ResponseEntity<ResultResponse> getTop15RecentSearchesPage() {
        final Page<SearchDto> searchDtoPage = searchService.getTop15RecentSearches();
        return ResponseEntity.ok(ResultResponse.of(GET_TOP_15_RECENT_SEARCH_SUCCESS ,searchDtoPage));
    }

    @DeleteMapping("/recent/all")
    public ResponseEntity<ResultResponse> deleteAllRecentSearch() {
        searchService.deleteAllRecentSearch();
        return ResponseEntity.ok(ResultResponse.of(DELETE_ALL_RECENT_SEARCH_SUCCESS));
    }
}
