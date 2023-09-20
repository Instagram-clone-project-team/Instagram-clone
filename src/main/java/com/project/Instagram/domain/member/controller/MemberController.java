package com.project.Instagram.domain.member.controller;

import com.project.Instagram.domain.member.dto.*;
import com.project.Instagram.domain.member.service.MemberService;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PatchMapping("/account/update")
    public ResponseEntity<ResultResponse> updateAccount(@Valid @RequestBody UpdateAccountRequest updateAccountRequest){
        memberService.updateAccount(updateAccountRequest);
        return ResponseEntity.ok(ResultResponse.of(UPDATE_ACCOUNT_SUCCESS));
    }

    @PatchMapping("/password/patch")
    public ResponseEntity<ResultResponse> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest){
        memberService.updatePassword(updatePasswordRequest);

        return ResponseEntity.ok(ResultResponse.of(UPDATE_PASSWORD_SUCCESS));
    }


    @PostMapping("/password/reset/email")
    public ResponseEntity<Object> sendPasswordCodeByEmail(@Valid @RequestBody SendPasswordEmailRequest sendPasswordEmailRequest){
        memberService.sendPasswordCodeEmail(sendPasswordEmailRequest);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/password/reset")
    public ResponseEntity<ResultResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest){
        memberService.resetPasswordByEmailCode(resetPasswordRequest);
        return ResponseEntity.ok(ResultResponse.of(RESET_PASSWORD_SUCCESS));
    }

}
