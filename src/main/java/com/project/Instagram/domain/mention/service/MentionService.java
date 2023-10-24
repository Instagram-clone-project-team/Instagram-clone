package com.project.Instagram.domain.mention.service;


import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import com.project.Instagram.global.util.StringExtractUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class MentionService {

    private final AlarmService alarmService;
    private final StringExtractUtil stringExtractUtil;

    public void checkMentionsFromPost(Member agent, String content, Post post) {
        alarmService.sendMentionPostAlarm(AlarmType.MENTION_POST, agent, stringExtractUtil.filteringMentions(content), post);
    }

    public void checkMentionsFromComment(Member agent, String text, Post post, Comment comment) {
        alarmService.sendMentionCommentAlarm(AlarmType.MENTION_COMMENT, agent, stringExtractUtil.filteringMentions(text), post, comment);
    }


}
