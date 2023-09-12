package com.project.Instagram.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    PASSWORD_MISMATCH(401,"계정 비밀번호가 일치하지 않습니다."),
    PASSWORD_SAME(400,"기존 비밀번호와 같습니다.");







    private final int status;
    private final String message;
}