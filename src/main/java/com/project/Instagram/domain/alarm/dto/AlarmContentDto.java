package com.project.Instagram.domain.alarm.dto;

import com.project.Instagram.domain.alarm.entity.Alarm;
import com.project.Instagram.domain.member.entity.Profile;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.project.Instagram.domain.alarm.dto.AlarmType.*;

@Getter
@NoArgsConstructor
public class AlarmContentDto extends AlarmDto {
    private Long postId;
    private String image;
    private String content;
    private List<String> mentionsOfContent = new ArrayList<>();
    private Set<String> hashtagsOfContent = new HashSet<>();


    public AlarmContentDto(Alarm alarm) {
        super(alarm.getId(), alarm.getType().name(), alarm.getType().createAlarmMessage(alarm), new Profile(alarm.getAgent()));
        this.postId = alarm.getPost().getId();
        this.image = alarm.getPost().getImage();
        this.content = getContent(alarm);
    }

    private String getContent(Alarm alarm) {
        final AlarmType type = alarm.getType();
        if (type.equals(COMMENT) || type.equals(LIKE_COMMENT) || type.equals(MENTION_COMMENT)) {
            return alarm.getComment().getText();
        } else if (type.equals(LIKE_POST) || type.equals(MENTION_POST)) {
            return alarm.getPost().getContent();
        }
        return "";
    }

    public void setMentionsOfContent(List<String> mentionsOfContent) {
        this.mentionsOfContent = mentionsOfContent;
    }

    public void setHashtagsOfContent(Set<String> hashtagsOfContent) {
        this.hashtagsOfContent = hashtagsOfContent;
    }
}
