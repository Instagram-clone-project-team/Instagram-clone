package com.project.Instagram.domain.follow.controller;

import com.project.Instagram.domain.follow.dto.FollowerDto;
import com.project.Instagram.domain.follow.service.FollowService;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import static com.project.Instagram.global.response.ResultCode.*;

@RestController
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/follow/{followMemberUsername}")
    public ResponseEntity<ResultResponse> follow(@PathVariable("followMemberUsername") @Validated @NotBlank(message = "사용자 이름이 필요합니다.") String followMemberUsername) {
        final boolean followResponse = followService.follow(followMemberUsername);

        if (followResponse) {
            return ResponseEntity.ok(ResultResponse.of(FOLLOW_SUCCESS, followResponse));
        } else {
            return ResponseEntity.ok(ResultResponse.of(FOLLOW_FAIL, followResponse));
        }
    }

    @DeleteMapping("/unfollow/{followMemberUsername}")
    public ResponseEntity<ResultResponse> unfollow(@PathVariable ("followMemberUsername") @Validated @NotBlank(message = "사용자 이름이 필요합니다.") String followMemberUsername) {
        final boolean followResponse = followService.unfollow(followMemberUsername);
        return ResponseEntity.ok(ResultResponse.of(UNFOLLOW_SUCCESS, followResponse));
    }

    @GetMapping("/following/{memberUsername}")
    public ResponseEntity<ResultResponse> getFollowingsPage(
            @Positive @RequestParam(value = "page", defaultValue = "1") int page,
            @Positive @RequestParam(value = "size", defaultValue = "5") int size,
            @PathVariable("memberUsername") @Validated @NotBlank(message = "사용자 이름이 필요합니다.") String memberUsername) {
        final PageListResponse<FollowerDto> followingResponse = followService.getFollowings(memberUsername, page - 1, size);
        return ResponseEntity.ok(ResultResponse.of(FOLLOWINGS_LIST_SUCCESS, followingResponse));
    }

    @GetMapping("/followers/{memberUsername}")
    public ResponseEntity<ResultResponse> getFollowersPage(
            @Positive @RequestParam(value = "page", defaultValue = "1") int page,
            @Positive @RequestParam(value = "size", defaultValue = "5") int size,
            @PathVariable("memberUsername") @Validated @NotBlank(message = "사용자 이름이 필요합니다.") String memberUsername) {
        final PageListResponse<FollowerDto> followerResponse = followService.getFollowers(memberUsername, page - 1, size);
        return ResponseEntity.ok(ResultResponse.of(FOLLOWERS_LIST_SUCCESS, followerResponse));
    }

    @GetMapping("/following-count/{memberUsername}")
    public ResponseEntity<ResultResponse> getFollowingCount(@PathVariable("memberUsername") @Validated @NotBlank(message = "사용자 이름이 필요합니다.") String memberUsername) {
        final int followingCount = followService.getFollowingCount(memberUsername);
        return ResponseEntity.ok(ResultResponse.of(FOLLOWING_COUNT_SUCCESS, followingCount));
    }

    @GetMapping("/follower-count/{memberUsername}")
    public ResponseEntity<ResultResponse> getFollowerCount(@PathVariable("memberUsername") @Validated @NotBlank(message = "사용자 이름이 필요합니다.") String memberUsername) {
        final int followerCount = followService.getFollowerCount(memberUsername);
        return ResponseEntity.ok(ResultResponse.of(FOLLOWER_COUNT_SUCCESS, followerCount));
    }
}
