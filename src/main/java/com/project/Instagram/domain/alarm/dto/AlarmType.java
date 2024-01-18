package com.project.Instagram.domain.alarm.dto;

import com.project.Instagram.domain.alarm.entity.Alarm;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmType {
    FOLLOW("{agent.username}님이 회원님을 팔로우하기 시작했습니다."),

    LIKE_POST("{agent.username}님이 회원님의 사진을 좋아합니다."),
    MENTION_POST("{agent.username}님이 게시물에서 회원님을 언급했습니다: {post.content}"),

    COMMENT("{agent.username}님이 댓글을 남겼습니다: {comment.text}"),
    LIKE_COMMENT("{agent.username}님이 회원님의 댓글을 좋아합니다: {comment.text}"),
    MENTION_COMMENT("{agent.username}님이 댓글에서 회원님을 언급했습니다: {comment.text}");

    private String messageTemplate;

    public String createAlarmMessage(Alarm alarm) {
        String message = messageTemplate;
        message = message.replace("{agent.username}", alarm.getAgent().getUsername());

        if (message.contains("{post.content}")) {
            message = message.replace("{post.content}", alarm.getPost().getContent());
        }

        if (message.contains("{comment.text}")) {
            message = message.replace("{comment.text}", alarm.getComment().getText());
        }
        return message;
    }
}

