package com.project.Instagram.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    PASSWORD_MISMATCH(401,"계정 비밀번호가 일치하지 않습니다."),
    PASSWORD_SAME(400,"기존 비밀번호와 같습니다."),
    MEMBER_NOT_FOUND(400,"존재 하지 않는 유저입니다."),
    PASSWORD_RESET_FAIL(400, "잘못되거나 만료된 코드입니다."),
    USERNAME_ALREADY_EXIST(400,"해당 유저네임은 이미 존재합니다.");


    private final int status;
    private final String message;
}