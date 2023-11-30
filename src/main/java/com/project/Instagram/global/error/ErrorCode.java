package com.project.Instagram.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //계정,유저
    PASSWORD_MISMATCH(401,"계정 비밀번호가 일치하지 않습니다."),
    PASSWORD_SAME(400,"기존 비밀번호와 같습니다."),
    MEMBER_NOT_FOUND(400,"존재 하지 않는 유저입니다."),
    USERNAME_ALREADY_EXIST(400,"해당 유저네임은 이미 존재합니다."),
    EMAIL_ALREADY_EXIST(400, "이미 가입된 이메일입니다."),
    LOGIN_INFORMATION_ERROR(500, "현재 사용자의 ID를 가져오는 중 문제가 발생했습니다."),
    MEMBER_NOT_LOGIN(400,"로그인 상태가 아닙니다."),
    INVALID_ROLE(500,"MemberRole의 role을 가져오는데 실패했습니다."),

    //토큰
    MEMBER_ID_REFRESH_TOKEN_DOES_NOT_EXIST(500, "해당 memberId의 refresh token 이 존재하지 않습니다."),
    REFRESH_NOT_ENTERED(400, "리프레시 토큰이 입력되지 않았습니다"),

    //이메일
    PASSWORD_RESET_FAIL(400, "잘못되거나 만료된 코드입니다."),
    MEMBERSHIP_CODE_NOT_FOUND(400, "해당 이메일의 인증 코드를 찾을 수 없습니다."),
    MEMBERSHIP_CODE_DOES_NOT_MATCH_EMAIL(400, "인증코드 이메일과 회원가입 이메일이 일치하지 않습니다."),
    EMAIL_SEND_FAIL(500, "이메일 전송 중 오류가 발생했습니다."),

    //게시글
    POST_NOT_FOUND(400,"해당 게시물은 존재하지 않습니다."),
    POST_DELETE_FAILED(400, "게시판은 게시자만 삭제할 수 있습니다."),
    POST_EDIT_FAILED(400, "게시판 수정은 게시자만 삭제할 수 있습니다."),
    POST_ALREADY_DELETED(400, "이미 삭제된 게시물입니다."),

    // 팔로우
    FOLLOW_MYSELF_FAIL(400, "자기 자신은 팔로우할 수 없습니다."),
    FOLLOW_ALREADY_EXIST(400,"이미 팔로우 한 유저입니다."),
    UNFOLLOW_MYSELF_FAIL(400, "자기 자신을 언팔로우할 수 없습니다."),
    UNFOLLOW_FAIL(400, "팔로우하지 않은 유저는 언팔로우를 할 수 없습니다."),
    FOLLOW_ALREADY_DELETED(400, "이미 언팔로우 한 사용자입니다."),

    //좋아요
    POSTLIKE_ALREADY_EXIST(500,"이미 해당 게시물에 좋아요하셨습니다."),
    POSTLIKE_NOT_FOUND(500,"해당 게시물 좋아요를 찾을 수 없습니다."),
    COMMENTLIKE_ALREADY_EXIST(500,"이미 해당 댓글에 좋아요 하셨습니다."),
    COMMENTLIKE_NOT_FOUND(500,"해당 댓글 좋아요를 찾을 수 없습니다."),

    //댓글
    COMMENT_NOT_FOUND(500, "해당 댓글이 존재하지 않습니다."),
    COMMENT_WRITER_FAIL(500, "해당 댓글의 작성자가 아닙니다."),
    //해시태그
    HASHTAG_MISMATCH(500,"해시태그는 #으로 시작 해야합니다."),
    HASHTAG_NOT_FOUND(500,"해당 해시태그를 찾을 수 없습니다."),
    // 알람
    MISMATCHED_ALARM_TYPE(400, "알람 타입이 올바르지 않습니다."),
    //공통
    ENTITY_TYPE_INVALID(500,"올바르지 않은 엔티티 타입입니다."),
    FILE_CONVERT_FAIL(500, "변환할 수 없는 파일입니다.");


    private final int status;
    private final String message;
}