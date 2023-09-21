package com.project.Instagram.domain.member.controller;

import com.project.Instagram.domain.member.dto.*;
import com.project.Instagram.domain.member.service.MemberService;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
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
            return ResponseEntity.ok(ResultResponse.of(SIGNUP_SUCCESS, true));
        } else {
            return ResponseEntity.ok(ResultResponse.of(EMAIL_VERIFICATION_FAIL, false));
        }
    }

    @PostMapping("accounts/email")
    public ResponseEntity<Object> sendAuthCodeByEmail(@Valid @RequestBody SendAuthEmailRequest sendAuthEmailRequest) {
        memberService.sendAuthEmail(sendAuthEmailRequest.getEmail());
        return ResponseEntity.ok(ResultResponse.of(SEND_EMAIL_SUCCESS));
    }

    @PatchMapping("/password/patch")
    public ResponseEntity<ResultResponse> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest){
        memberService.updatePassword(updatePasswordRequest);

        return ResponseEntity.ok(ResultResponse.of(UPDATE_PASSWORD_SUCCESS));
    }


    @PostMapping(value = "/logout")
    public ResponseEntity<ResultResponse> logout() {
        memberService.logout();
        return ResponseEntity.ok(ResultResponse.of(LOGOUT_SUCCESS));
    }
}
