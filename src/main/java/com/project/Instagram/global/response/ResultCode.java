package com.project.Instagram.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    //Member
    UPDATE_PASSWORD_SUCCESS(200,"비밀번호 변경이 완료되었습니다."),
    REGISTER_SUCCESS(200, "회원가입에 성공하였습니다."),
    DELETE_SUCCESS(200,"회원 탈퇴 완료되었습니다."),
    RESET_PASSWORD_SUCCESS(200, "비밀번호 리셋 완료되었습니다."),
    UPDATE_ACCOUNT_SUCCESS(200,"계정 정보 변경이 완료되었습니다.");


    //Post





    private final int status;

    private final String message;
}
