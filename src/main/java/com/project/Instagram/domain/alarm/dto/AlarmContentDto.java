package com.project.Instagram.domain.alarm.dto;

import com.project.Instagram.domain.alarm.entity.Alarm;
import com.project.Instagram.domain.member.entity.Profile;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.project.Instagram.domain.alarm.dto.AlarmType.*;

@Getter
@NoArgsConstructor
public class AlarmContentDto extends AlarmDto{
    private Long postId;
    private String postImage;
    private String content;
    private List<String> mentionsOfContent = new ArrayList<>();

    public AlarmContentDto(Alarm alarm) {
        super(alarm.getId(), alarm.getType().name(), alarm.getType().getMessage(), new Profile(alarm.getAgent()));
        this.postId = alarm.getPost().getId();
        this.postImage = alarm.getPost().getImage();
        this.content = getContent(alarm);
    }

    private String getContent(Alarm alarm) {
        final AlarmType type = alarm.getType();
        if (type.equals(LIKE_POST) || type.equals(MENTION_POST)) return alarm.getPost().getContent();
        return "";
//        if (type.equals(COMMENT) || type.equals(LIKE_COMMENT) || type.equals(MENTION_COMMENT)) {
//            return alarm.getCommenet().getContent();
//        } else if (type.equals(LIKE_POST) || type.equals(MENTION_POST)) {
//            return alarm.getPost().getContent();
//        }
//        return "";
    }

    public void setMentionsOfContent(List<String> mentionsOfContent) {
        this.mentionsOfContent = mentionsOfContent;
    }
}
