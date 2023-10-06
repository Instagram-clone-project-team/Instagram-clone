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
    USERNAME_ALREADY_EXIST(400,"해당 유저네임은 이미 존재합니다."),
    MEMBERSHIP_CODE_NOT_FOUND(400, "해당 이메일의 인증 코드를 찾을 수 없습니다."),
    MEMBERSHIP_CODE_DOES_NOT_MATCH_EMAIL(400, "인증코드 이메일과 회원가입 이메일이 일치하지 않습니다."),
    EMAIL_SEND_FAIL(500, "이메일 전송 중 오류가 발생했습니다."),
    FILE_CONVERT_FAIL(500, "변환할 수 없는 파일입니다."),
    MEMBER_ID_REFRESH_TOKEN_DOES_NOT_EXIST(500, "해당 memberId의 refresh token 이 존재하지 않습니다."),
    LOGIN_INFORMATION_ERROR(500, "현재 사용자의 ID를 가져오는 중 문제가 발생했습니다."),
    REFRESH_NOT_ENTERED(400, "리프레시 토큰이 입력되지 않았습니다"),
    POST_DELETE_FAILED(400, "게시판은 게시자만 삭제할 수 있습니다."),
    POST_EDIT_FAILED(400, "게시판 수정은 게시자만 삭제할 수 있습니다."),
    POST_NOT_FOUND(400, "해당 아이디의 게시물이 존재하지 않습니다."),
    POST_ALREADY_DELETED(400, "이미 삭제된 게시물입니다.");
    INVALID_ROLE(500,"MemberRole의 role을 가져오는데 실패했습니다.");

    private final int status;
    private final String message;
}