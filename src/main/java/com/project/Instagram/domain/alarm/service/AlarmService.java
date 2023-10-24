package com.project.Instagram.domain.alarm.service;

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
import com.project.Instagram.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static com.project.Instagram.domain.alarm.dto.AlarmType.*;
import static com.project.Instagram.global.error.ErrorCode.MISMATCHED_ALARM_TYPE;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

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
        for (Member targetMember : target) {
            Alarm alarm = Alarm.builder()
                    .type(type)
                    .agent(agent)
                    .target(targetMember)
                    .post(post)
                    .build();
            alarmRepository.save(alarm);
        }
    }

    @Transactional
    public void sendMentionCommentAlarm(AlarmType type, Member agent, List<String> targets, Post post, Comment comment) {
        if (!type.equals(MENTION_COMMENT)) throw new BusinessException(MISMATCHED_ALARM_TYPE);
        List<Member> target = memberRepository.findAllByUsernameIn(targets);
        for (Member targetMember : target) {
            Alarm alarm = Alarm.builder()
                    .type(type)
                    .agent(agent)
                    .target(targetMember)
                    .post(post)
                    .comment(comment)
                    .build();
            alarmRepository.save(alarm);
        }
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
}
