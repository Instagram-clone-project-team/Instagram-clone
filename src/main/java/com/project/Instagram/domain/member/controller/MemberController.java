package com.project.Instagram.domain.member.controller;

import com.project.Instagram.domain.member.dto.*;
import com.project.Instagram.domain.member.service.MemberService;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.Instagram.global.response.ResultCode.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping(value = "accounts/sign-up")
    public ResponseEntity<Object> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        final boolean membership = memberService.signUp(signUpRequest);
        if (membership) {
            return ResponseEntity.ok(HttpStatus.CREATED);
        } else {
            return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("accounts/email")
    public ResponseEntity<Object> sendAuthCodeByEmail(@Valid @RequestBody SendAuthEmailRequest sendAuthEmailRequest) {
        memberService.sendAuthEmail(sendAuthEmailRequest.getUsername(), sendAuthEmailRequest.getEmail());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<Object> logout(@RequestParam String refreshToken) {
        memberService.logout(refreshToken);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
