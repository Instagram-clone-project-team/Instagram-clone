package com.project.Instagram.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    //Member
    UPDATE_PASSWORD_SUCCESS(200,"비밀번호 변경이 완료되었습니다."),
    SIGNUP_SUCCESS(200, "회원가입에 성공하였습니다."),
    DELETE_SUCCESS(200,"회원 탈퇴 완료되었습니다."),
    RESET_PASSWORD_SUCCESS(200, "비밀번호 리셋 완료되었습니다."),
    UPDATE_ACCOUNT_SUCCESS(200,"계정 정보 변경이 완료되었습니다."),
    SEND_EMAIL_SUCCESS(200,  "인증코드 이메일을 전송하였습니다."),
    EMAIL_VERIFICATION_FAIL(400, "이메일 인증에 실패했습니다."),
    LOGOUT_SUCCESS(200, "로그아웃에 성공하였습니다."),



    //Post
    POST_CREATE_SUCCESS(200, "게시글 생성에 성공하였습니다.");




    private final int status;

    private final String message;
}
