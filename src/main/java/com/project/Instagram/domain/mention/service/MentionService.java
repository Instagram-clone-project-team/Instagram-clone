package com.project.Instagram.domain.mention.service;


import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.mention.MentionType;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MentionService {

    private final PostRepository postRepository;
    private final AlarmService alarmService;

    public void createMentions(long sender, String content, AlarmType connectedTarget, long connectedId) {
        List<String> mention_usernames = filteringMentions(content);
        Post post = postRepository.findById(connectedId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        alarmService.sendMentionPostAlarm(connectedTarget, mention_usernames, post);
    }

    private List<String> filteringMentions(String content) {
        List<String> mention_usernames = new ArrayList<>();
        String regex = "@[0-9a-zA-Z가-힣ㄱ-ㅎ_]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matching = pattern.matcher(content);

        while (matching.find()) {
            mention_usernames.add(matching.group().substring(1));
        }
        return mention_usernames;
    }
}
