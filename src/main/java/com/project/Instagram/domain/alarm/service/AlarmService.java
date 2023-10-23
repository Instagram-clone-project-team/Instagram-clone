package com.project.Instagram.domain.alarm.service;

import com.project.Instagram.domain.alarm.dto.AlarmContentDto;
import com.project.Instagram.domain.alarm.dto.AlarmDto;
import com.project.Instagram.domain.alarm.dto.AlarmFollowDto;
import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.entity.Alarm;
import com.project.Instagram.domain.alarm.repository.AlarmRepository;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostLike;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.Instagram.domain.alarm.dto.AlarmType.*;
import static com.project.Instagram.global.error.ErrorCode.MISMATCHED_ALARM_TYPE;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final SecurityUtil securityUtil;

    // 댓글 해시태그 멘션 합쳐야함
//    @Transactional(readOnly = true)
//    public Page<AlarmDto> getAlarms(int page, int size) {
//        final Long loginMember = securityUtil.getLoginMember().getId();
//        final Pageable pageable = PageRequest.of(page, size);
//
//        final Page<Alarm> alarmsPage = alarmRepository.findAlarmPageByMemberId(pageable, loginMember);
//        final List<Alarm> alarms = alarmsPage.getContent();
//        final List<Long> agentIds = alarms.stream()
//                .filter(a -> a.getType().equals(FOLLOW))
//                .map(a -> a.getAgent().getId())
//                .collect(Collectors.toList());
//
//        final List<Follow> follows = followRepository.findFollows(loginMember, agentIds);
//        final Map<Long, Follow> followMap = follows.stream()
//                .collect(Collectors.toMap(f -> f.getFollowMember().getId(), f -> f));
//
//        final List<AlarmDto> content = convertToDto(alarms, followMap);
//
//        return new PageImpl<>(content, pageable, alarmsPage.getTotalElements());
//    }

    @Transactional
    public void sendFollowAlarm(Member agent, Member target, Follow follow) {
        final Alarm alarm = Alarm.builder()
                .type(FOLLOW)
                .agent(agent)
                .target(target)
                .follow(follow)
                .build();

        alarmRepository.save(alarm);
    }

    @Transactional
    public void sendPostLikeAlarm(AlarmType type, Member agent, Member target, PostLike post) {
        if (!type.equals(LIKE_POST)) throw new BusinessException(MISMATCHED_ALARM_TYPE);
        final Alarm alarm = Alarm.builder()
                .type(type)
                .agent(agent)
                .target(target)
                .post(post.getPost())
                .build();

        alarmRepository.save(alarm);
    }

    @Transactional
    public void sendCommentAlarm(AlarmType type, Member agent, Member target, Post post, Comment comment) {
        if (!type.equals(COMMENT) && !type.equals(LIKE_COMMENT) && !type.equals(MENTION_COMMENT)) {
            throw new BusinessException(MISMATCHED_ALARM_TYPE);
        }

        final Alarm alarm = Alarm.builder()
                .type(type)
                .agent(agent)
                .target(target)
                .post(post)
                .comment(comment)
                .build();

        alarmRepository.save(alarm);
    }

    @Transactional
    public void sendMentionPostAlarm(AlarmType type, Member agent, List<String> targets, Post post) {
        if (!type.equals(MENTION_POST)) throw new BusinessException(MISMATCHED_ALARM_TYPE);
        List<Member> target = memberRepository.findAllByUsernameIn(targets);
        alarmRepository.saveMentionPostAlarms(agent, target, post);
    }

    @Transactional
    public void sendMentionCommentAlarm(AlarmType type, Member agent, List<String> targets, Post post, Comment comment) {
        if (!type.equals(MENTION_COMMENT)) throw new BusinessException(MISMATCHED_ALARM_TYPE);
        List<Member> target = memberRepository.findAllByUsernameIn(targets);
        alarmRepository.saveMentionCommentAlarms(agent, target, post, comment);

    }

    @Transactional
    public void deletePostLikeAlarm(AlarmType type, Member agent, Member target, Post post) {
        alarmRepository.deleteByTypeAndAgentAndTargetAndPost(type, agent, target, post);
    }

    @Transactional
    public void deleteCommentLikeAlarm(AlarmType type, Member agent, Member target, Comment comment) {
        alarmRepository.deleteByTypeAndAgentAndTargetAndComment(type, agent, target, comment);
    }

    @Transactional
    public void deleteFollowAlarm(Member agent, Member target, Follow follow) {
        alarmRepository.deleteByTypeAndAgentAndTargetAndFollow(FOLLOW, agent, target, follow);
    }

    @Transactional
    public void deleteAllPostAlarm(Post post) {
        final List<Alarm> alarms = alarmRepository.findAllByPost(post);
        alarmRepository.deleteAllInBatch(alarms);
    }

    @Transactional
    public void deleteAllCommentAlarm(List<Comment> comments) {
        final List<Alarm> alarms = alarmRepository.findAllByCommentIn(comments);
        alarmRepository.deleteAllInBatch(alarms);
    }


//    // 해시태그 댓글 합쳐야함
//    private List<AlarmDto> convertToDto(List<Alarm> alarms, Map<Long, Follow> followMap) {
//        return alarms.stream()
//                .map(alarm -> {
//                    final AlarmType type = alarm.getType();
//                    if (type.equals(FOLLOW)) {
//                        return new AlarmFollowDto(alarm, followMap.containsKey(alarm.getAgent().getId()));
//                    } else {
//                        final AlarmContentDto dto = new AlarmContentDto(alarm);
//                        if (type.equals(COMMENT) || type.equals(LIKE_COMMENT) || type.equals(MENTION_COMMENT)) {
//                            setMentionAndHashtagList(alarm.getComment().getContent(), dto);
//                        } else if (type.equals(LIKE_POST) || type.equals(MENTION_POST)) {
//                            setMentionAndHashtagList(alarm.getPost().getContent(), dto);
//                        }
//                        return dto;
//                    }
//                })
//                .collect(Collectors.toList());
//    }

//    // 해시태그 합쳐야함
//    private void setMentionAndHashtagList(String content, AlarmContentDto dto) {
//        final List<String> mentionedUsernames = stringExtractUtil.extractMentionsWithExceptList(content, List.of());
//        final List<String> existentUsernames = memberRepository.findAllByUsernameIn(mentionedUsernames).stream()
//                .map(Member::getUsername)
//                .collect(Collectors.toList());
//        dto.setMentionsOfContent(existentUsernames);
//    }
}
