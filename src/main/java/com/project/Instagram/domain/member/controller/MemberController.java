package com.project.Instagram.domain.member.controller;

import com.project.Instagram.domain.member.dto.*;
import com.project.Instagram.domain.member.entity.Profile;
import com.project.Instagram.domain.member.service.MemberService;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import javax.validation.constraints.Positive;

import static com.project.Instagram.global.response.ResultCode.*;

@Validated
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/accounts/sign-up")
    public ResponseEntity<Object> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        final boolean membership = memberService.signUp(signUpRequest);
        if (membership) {
            return ResponseEntity.ok(ResultResponse.of(SIGNUP_SUCCESS, true));
        } else {
            return ResponseEntity.ok(ResultResponse.of(EMAIL_VERIFICATION_FAIL, false));
        }
    }

    @PostMapping("/accounts/email")
    public ResponseEntity<Object> sendAuthCodeByEmail(@Valid @RequestBody SendAuthEmailRequest sendAuthEmailRequest) {
        memberService.sendAuthEmail(sendAuthEmailRequest.getEmail());
        return ResponseEntity.ok(ResultResponse.of(SEND_EMAIL_SUCCESS));
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

    @DeleteMapping("/logout")
    public ResponseEntity<ResultResponse> logout() {
        memberService.logout();
        return ResponseEntity.ok(ResultResponse.of(LOGOUT_SUCCESS));
    }

    @DeleteMapping("/member")
    public ResponseEntity<ResultResponse> delete() {
        memberService.deleteMember();
        return ResponseEntity.ok(ResultResponse.of(DELETE_SUCCESS));
    }

    @GetMapping("/member/{username}")
    public ResponseEntity<ResultResponse> getProfile(@PathVariable("username") String username ) {
        memberService.getProfile(username);
        return ResponseEntity.ok(ResultResponse.of(GET_PROFILE_SUCCESS));
    }

    @GetMapping("/member")
    public ResponseEntity<ResultResponse> getProfilesPage(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                          @Positive @RequestParam(value = "size", defaultValue = "5") int size) {
        PageListResponse<Profile> response = memberService.getProfilePageList(page - 1, size);
        return ResponseEntity.ok(ResultResponse.of(LOOK_UP_MEMBER_LIST_SUCCESS, response));
    }

    @PostMapping("/token/reissue")
    public ResponseEntity<ResultResponse> reissueRefreshToken(HttpServletResponse response,
            @RequestHeader(value="refresh", defaultValue = "") String refresh,
            @RequestHeader(value="Authorization", defaultValue = "") String access){
        Map<String, String> result=memberService.reissueAccessToken(access, refresh);
        response.setHeader("Authorization", "Bearer " +result.get("access"));
        response.setHeader("Refresh", result.get("refresh"));
        return ResponseEntity.ok(ResultResponse.of(REISSUE_JWT_SUCCESS));
    }

}
