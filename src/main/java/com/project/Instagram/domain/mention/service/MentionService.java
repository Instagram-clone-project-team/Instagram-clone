package com.project.Instagram.domain.mention.service;

import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.global.util.StringExtractUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MentionService {

    private final AlarmService alarmService;
    private final StringExtractUtil stringExtractUtil;

    public void checkMentionsFromPost(Member agent, String content, Post post) {
        alarmService.sendMentionPostAlarm(AlarmType.MENTION_POST, agent, stringExtractUtil.filteringMentions(content), post);
    }

    public void checkUpdateMentionsFromPost(Member agent, String beforeText, String afterText, Post post) {
        List<String> beforeMentions = stringExtractUtil.filteringMentions(beforeText);
        List<String> afterMentions = stringExtractUtil.filteringMentions(afterText);
        List<String> deletedAfterMentions = afterMentions.stream()
                .filter(am -> beforeMentions.stream().noneMatch(bm -> am.equals(bm)))
                .collect(Collectors.toList());
        alarmService.sendMentionPostAlarm(AlarmType.MENTION_POST, agent, deletedAfterMentions, post);
    }

    public void checkMentionsFromComment(Member agent, String text, Post post, Comment comment) {
        alarmService.sendMentionCommentAlarm(AlarmType.MENTION_COMMENT, agent, stringExtractUtil.filteringMentions(text), post, comment);
    }


    public void checkUpdateMentionsFromComment(Member agent, String beforeText, String afterText, Post post, Comment comment) {
        List<String> beforeMentions = stringExtractUtil.filteringMentions(beforeText);
        List<String> afterMentions = stringExtractUtil.filteringMentions(afterText);
        List<String> deletedAfterMentions = afterMentions.stream()
                .filter(am -> beforeMentions.stream().noneMatch(bm -> am.equals(bm)))
                .collect(Collectors.toList());
        alarmService.sendMentionCommentAlarm(AlarmType.MENTION_COMMENT, agent, deletedAfterMentions, post, comment);
    }
}
