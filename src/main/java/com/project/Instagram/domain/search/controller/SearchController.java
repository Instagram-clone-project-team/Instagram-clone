package com.project.Instagram.domain.search.controller;

import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.search.dto.HashTagResponseDto;
import com.project.Instagram.domain.search.dto.SearchDto;
import com.project.Instagram.domain.search.service.SearchService;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    // mewluee

    // DongYeopMe
    @GetMapping("/auto/member")
    public ResponseEntity<ResultResponse> getAutoMember(@RequestParam String text){
        final List<Profile> memberResponse = searchService.getAutoMember(text);

        return ResponseEntity.ok(ResultResponse.of(MEMBER_AUTO_COMPLETE,memberResponse));
    }
    @GetMapping("/auto/hashtag")
    public ResponseEntity<ResultResponse> getAutoHashTag(@RequestParam String text){
        final List<HashTagResponseDto> hashTagResponseDtos = searchService.getAutoHashtag(text);

        return ResponseEntity.ok(ResultResponse.of(HASHTAG_AUTO_COMPLETE,hashTagResponseDtos));
    }
    @PostMapping("/AfterJoin")
    public ResponseEntity<ResultResponse> processAfterSearchAndJoin(@RequestParam String type,@RequestParam String name){
        searchService.processAfterSearchAndJoin(type,name);

        return ResponseEntity.ok(ResultResponse.of(SUCCESS_PROCESSING_AFTER_SEARCH_JOIN));
    }

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
