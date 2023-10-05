package com.project.Instagram.domain.follow.controller;

import com.project.Instagram.domain.follow.service.FollowService;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

import static com.project.Instagram.global.response.ResultCode.*;

@RestController
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followServcice;

    @PostMapping("/follow/{followMemberUsername}")
    public ResponseEntity<ResultResponse> follow(@PathVariable("followMemberUsername") @Validated @NotBlank(message = "사용자 이름이 필요합니다.") String followMemberUsername) {
        final boolean followResponse = followServcice.follow(followMemberUsername);

        if (followResponse) {
            return ResponseEntity.ok(ResultResponse.of(FOLLOW_SUCCESS, followResponse));
        } else {
            return ResponseEntity.ok(ResultResponse.of(FOLLOW_FAIL, followResponse));
        }
    }
}
