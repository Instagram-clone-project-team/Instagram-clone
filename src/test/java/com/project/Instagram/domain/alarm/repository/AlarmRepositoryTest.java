package com.project.Instagram.domain.alarm.repository;

import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.entity.Alarm;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.repository.CommentRepository;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostLike;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.config.QuerydslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.Instagram.domain.alarm.dto.AlarmType.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AlarmRepositoryTest {

    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private FollowRepository followRepository;

    @BeforeEach
    void setUp() {
        alarmRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        followRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("게시물 좋아요 알람 저장 테스트")
    void testSendPostLikeAlarmSaveAlarm() {
        // given
        AlarmType type = LIKE_POST;
        Member agent = Member.builder()
                .username("agentUsername")
                .name("agentname")
                .password("djkasdjasdj123")
                .build();

        Member target = Member.builder()
                .username("targetUsername")
                .build();

        Post post = Post.builder()
                .member(agent)
                .image("postImage")
                .content("postContent")
                .build();

        PostLike postLike = PostLike.builder()
                .member(target)
                .post(post)
                .build();

        Alarm alarm = Alarm.builder()
                .type(type)
                .agent(agent)
                .target(target)
                .post(postLike.getPost())
                .build();

        // when
        Alarm savedAlarm = alarmRepository.save(alarm);

        // then
        assertThat(savedAlarm).isNotNull();
        assertThat(savedAlarm.getType()).isEqualTo(type);
        assertThat(savedAlarm.getAgent()).isEqualToComparingFieldByField(agent);
        assertThat(savedAlarm.getTarget()).isEqualToComparingFieldByField(target);
        assertThat(savedAlarm.getPost()).isEqualToComparingFieldByField(postLike.getPost());

    }

    @Test
    @DisplayName("댓글 알람 저장 확인")
    void testSendCommentAlarmSaveAlarm() {
        // given
        AlarmType type = COMMENT;

        Member agent = Member.builder()
                .username("agentUsername")
                .name("agentname")
                .password("djkasdjasdj123")
                .build();

        Member target = Member.builder()
                .username("targetUsername")
                .build();

        Post post = Post.builder()
                .member(agent)
                .image("postImage")
                .content("postContent")
                .build();
        post.setId(1L);

        Comment comment = Comment.builder()
                .writer(agent)
                .postId(post.getId())
                .text("Comment text")
                .build();

        final Alarm alarm = Alarm.builder()
                .type(type)
                .agent(agent)
                .target(target)
                .post(post)
                .comment(comment)
                .build();

        // when
        Alarm savedAlarm = alarmRepository.save(alarm);

        // then
        assertThat(savedAlarm).isNotNull();
        assertThat(savedAlarm.getType()).isEqualTo(type);
        assertThat(savedAlarm.getAgent()).isEqualToComparingFieldByField(agent);
        assertThat(savedAlarm.getTarget()).isEqualToComparingFieldByField(target);
        assertThat(savedAlarm.getPost()).isEqualToComparingFieldByField(post);
        assertThat(savedAlarm.getComment()).isEqualToComparingFieldByField(comment);

    }

    @Test
    @DisplayName("댓글 언급 알람 저장 확인")
    void testSendMentionCommentAlarmSaveAlarm() {
        // given
        AlarmType type = MENTION_COMMENT;

        Member agent = Member.builder()
                .username("agentUsername")
                .name("agentname")
                .password("djkasdjasdj123")
                .build();

        List<String> targetUsernames = Arrays.asList("targetUsername1", "targetUsername2", "targetUsername3");
        List<Member> targetMembers = targetUsernames.stream()
                .map(username -> Member.builder()
                        .username(username)
                        .name("testname")
                        .password("djkasdjasdj123")
                        .build())
                .collect(Collectors.toList());

        Post post = Post.builder()
                .member(agent)
                .image("postImage")
                .content("postContent")
                .build();
        post.setId(1L);

        Comment comment = Comment.builder()
                .writer(agent)
                .postId(post.getId())
                .text("Comment text")
                .build();

        memberRepository.save(agent);
        memberRepository.saveAll(targetMembers);
        postRepository.save(post);
        commentRepository.save(comment);

        // when
        for (Member targetMember : targetMembers) {
            Alarm alarm = Alarm.builder()
                    .type(type)
                    .agent(agent)
                    .target(targetMember)
                    .post(post)
                    .comment(comment)
                    .build();
            alarmRepository.save(alarm);
        }

        // then
        List<Alarm> savedAlarms = alarmRepository.findAll();
        assertThat(savedAlarms).hasSize(targetMembers.size());

        for (int i = 0; i < targetMembers.size(); i++) {
            assertThat(savedAlarms.get(i).getType()).isEqualTo(type);
            assertThat(savedAlarms.get(i).getAgent()).isEqualToComparingOnlyGivenFields(agent, "id", "username", "createdAt", "modifiedAt");
            assertThat(savedAlarms.get(i).getTarget()).isEqualToComparingOnlyGivenFields(targetMembers.get(i), "id", "username", "createdAt", "modifiedAt");
            assertThat(savedAlarms.get(i).getPost()).isEqualToComparingFieldByField(post);
            assertThat(savedAlarms.get(i).getComment()).isEqualToComparingFieldByField(comment);
        }
    }

    @Test
    @DisplayName("팔로우 알람 삭제 테스트")
    void deleteByTypeAndAgentAndTargetAndFollow() {
        // given
        Member agent = Member.builder()
                .username("agentUsername")
                .name("agentname")
                .password("djkasdjasdj123")
                .build();

        Member target = Member.builder()
                .username("targetUsername")
                .name("targetname")
                .password("djkasdjasdj123")
                .build();

        Follow follow = Follow.builder()
                .followMember(agent)
                .followMember(target)
                .build();

        memberRepository.save(agent);
        memberRepository.save(target);
        followRepository.save(follow);

        Alarm savedFollowAlarm = Alarm.builder()
                .type(FOLLOW)
                .agent(agent)
                .target(target)
                .follow(follow)
                .build();
        alarmRepository.save(savedFollowAlarm);

        // when
        alarmRepository.deleteByTypeAndAgentAndTargetAndFollow(FOLLOW, agent, target, follow);

        // then
        List<Alarm> alarmsAfterDeletion = alarmRepository.findAll();
        assertThat(alarmsAfterDeletion).isEmpty();
    }

    @Test
    @DisplayName("게시물 알림 관련 모든 데이터 삭제")
    void deleteAllPostAlarm() {
        // given
        Member agent = Member.builder()
                .username("agentUsername")
                .name("agentname")
                .password("djkasdjasdj123")
                .build();
        Member target = Member.builder()
                .username("targetUsername")
                .name("targetname")
                .password("djkasdjasdj123")
                .build();
        memberRepository.saveAll(List.of(agent, target));

        Post post = Post.builder()
                .member(agent)
                .image("postImage")
                .content("postContent")
                .build();
        post.setId(1L);
        postRepository.save(post);

        Comment comment = Comment.builder()
                .writer(agent)
                .postId(post.getId())
                .text("Comment text")
                .build();
        commentRepository.save(comment);

        Alarm alarm1 = Alarm.builder()
                .type(LIKE_POST)
                .agent(agent)
                .target(target)
                .post(post)
                .build();

        Alarm alarm2 = Alarm.builder()
                .type(COMMENT)
                .agent(agent)
                .target(target)
                .post(post)
                .comment(comment)
                .build();

        alarmRepository.saveAll(List.of(alarm1, alarm2));

        // when
        List<Alarm> alarmsToDelete = alarmRepository.findAllByPost(post);
        alarmRepository.deleteAllInBatch(alarmsToDelete);

        // then
        List<Alarm> remainingAlarms = alarmRepository.findAllByPost(post);
        assertThat(remainingAlarms).isEmpty();
    }
}