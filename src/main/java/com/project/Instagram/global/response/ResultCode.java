package com.project.Instagram.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    // 멤버
    UPDATE_PASSWORD_SUCCESS(200,"비밀번호 변경이 완료되었습니다."),
    SIGNUP_SUCCESS(200, "회원가입에 성공하였습니다."),
    DELETE_SUCCESS(200,"회원 탈퇴 완료되었습니다."),
    RESET_PASSWORD_SUCCESS(200, "비밀번호 리셋 완료되었습니다."),
    UPDATE_ACCOUNT_SUCCESS(200,"계정 정보 변경이 완료되었습니다."),
    SEND_EMAIL_SUCCESS(200,  "인증코드 이메일을 전송하였습니다."),
    EMAIL_VERIFICATION_FAIL(400, "이메일 인증에 실패했습니다."),
    LOOK_UP_MEMBER_LIST_SUCCESS(200, "계정 목록 조회가 완료되었습니다."),
    LOGOUT_SUCCESS(200, "로그아웃에 성공하였습니다."),
    REISSUE_JWT_SUCCESS(200, "토큰 재발급이 완료되었습니다."),
    GET_PROFILE_SUCCESS(200, "프로필 조회에 성공하였습니다."),




    // 게시글
    GET_POST_PAGE_SUCCESS(200,"게시물 목록 조회가 완료되었습니다."),
    GET_POST_SUCCESS(200,"게시물 상세 조회가 완료되었습니다."),
    GET_POST_USER_PAGE_SUCCESS(200,"해당 유저 게시물 목록 조회가 완료되었습니다."),
    POST_CREATE_SUCCESS(200, "게시글 생성에 성공하였습니다."),
    DELETE_POST_SUCCESS(200, "게시물 삭제에 성공하였습니다."),
    UPDATE_POST_SUCCESS(200, "게시물 수정이 완료되었습니다."),
    GET_FOLLOWED_POSTS_SUCCESS(200, "팔로우한 유저들의 게시물 조회가 완료되었습니다."),


    //댓글
    COMMENT_CREATE_SUCCESS(200, "댓글 생성에 성공하였습니다."),
    COMMENT_REPLY_SUCCESS(200, "답글 생성에 성공하였습니다."),
    COMMENT_UPDATE_SUCCESS(200, "댓글 수정에 성공하였습니다."),
    COMMENT_DELETE_SUCCESS(200, "댓글 삭제에 성공하였습니다."),
    COMMENT_GET_SUCCESS(200, "댓글 조회에 성공하였습니다."),



    // 팔로우
    FOLLOW_FAIL(400, "팔로우에 실패했습니다."),
    FOLLOW_SUCCESS(200, "팔로우에 성공했습니다."),
    UNFOLLOW_SUCCESS(200, "언팔로우에 성공했습니다."),
    FOLLOWINGS_LIST_SUCCESS(200, "팔로잉 목록 조회가 성공했습니다."),
    FOLLOWERS_LIST_SUCCESS(200, "팔로워 목록 조회가 성공했습니다."),
    FOLLOWING_COUNT_SUCCESS(200, "팔로잉 수 조회가 성공했습니다."),
    FOLLOWER_COUNT_SUCCESS(200, "팔로워 수 조회가 성공했습니다."),


    // 좋아요
    POST_LIKE_SUCCESS(200,"좋아요 성공하였습니다."),
    POST_UNLIKE_SUCCESS(200,"좋아요 취소하였습니다."),
    GET_POSTLIKE_USERS_SUCCESS(200,"해당 게시글 좋아요한 유저 조회가 완료되었습니다."),
    COMMENT_LIKE_SUCCESS(200, "댓글 좋아요 성공하였습니다."),
    COMMENT_UNLIKE_SUCCESS(200, "댓글 좋아요 취소하였습니다."),
    GET_COMMENTLIKE_USERS_SUCCESS(200,"해당 댓글에 좋아요한 유저 조회가 완료되었습니다."),
    // 알람
    GET_ALARMS_SUCCESS(200, "알림 조회에 성공하였습니다."),
    ALARMS_CONNECT_SUCCESS(200, "알림 연결에 성공하였습니다."),

    // 검색
    SEARCH_SUCCESS(200, "검색에 성공하였습니다."),
    MEMBER_AUTO_COMPLETE(200,"멤버 자동 완성 성공하였습니다."),
    HASHTAG_AUTO_COMPLETE(200,"해시태그 자동 완성 성공하였습니다."),
    DELETE_ALL_RECENT_SEARCH_SUCCESS(200, "최근 검색 기록 전체 삭제에 성공하였습니다."),
    SUCCESS_PROCESSING_AFTER_SEARCH_JOIN(200,"최근 검색 기록 업데이트 성공하였습니다."),
    GET_TOP_15_RECENT_SEARCH_SUCCESS(200, "최근 검색 기록 15개 조회에 성공하였습니다."),
    DELETE_RECENT_SEARCH_SUCCESS(200, "최근검색 삭제에 성공했습니다."),
    GET_RECENTSEARCH_LIST_SUCCESS(200, "최근검색 목록 조회가 완료되었습니다."),
    GET_RECOMMEND_MEMBER_LIST_SUCCESS(200, "팔로잉 추천 회원 조회가 완료되었습니다."),
    GET_CHAT_MESSAGES_SUCCESS(200,  "채팅 메시지 목록 조회에 성공하였습니다."),
    GET_CHAT_ROOMS_SUCCESS(200, "채팅방 조회에 성공하였습니다."),
    DELETE_JOIN_ROOM_SUCCESS(200, "채팅방을 나갔습니다."),
    CREATE_CHAT_ROOM_SUCCESS(200,  "채팅방 생성에 성공하였습니다.");


    private final int status;
    private final String message;
}
