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
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.repository.PostRepository;
import com.project.Instagram.global.config.QuerydslConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void deleteAll(){
        alarmRepository.deleteAll();
    }

    @Test
    @DisplayName("[alarm repo] save")
    void test_save_alarm() {
        // given
        Member agent = new Member();
        agent.setId(1L);
        Member target = new Member();
        target.setId(2L);
        Follow follow = Follow.builder()
                .member(agent)
                .followMember(target)
                .build();

        Alarm alarm = Alarm.builder()
                .type(AlarmType.FOLLOW)
                .agent(agent)
                .target(target)
                .follow(follow)
                .build();

        //when
        Alarm savedAlarm = alarmRepository.save(alarm);

        // then
        Assertions.assertEquals(agent, savedAlarm.getAgent());
        Assertions.assertEquals(target, savedAlarm.getTarget());
        Assertions.assertEquals(agent.getId(), savedAlarm.getFollow().getMember().getId());
        Assertions.assertEquals(target.getId(), savedAlarm.getFollow().getFollowMember().getId());
    }

    @Test
    @DisplayName("[alarm repo] delete by post like alarm")
    void test_delete_by_post_like_alarm() {
        //given
        Member agent = new Member();
        agent.setId(1L);
        agent.setUsername("luee");
        agent.setName("haneul");
        agent.setPassword("lueepwd1234");
        memberRepository.save(agent);

        Member target = new Member();
        target.setId(2L);
        target.setUsername("luee2");
        target.setName("haneul");
        target.setPassword("lueepwd1234");
        memberRepository.save(target);

        Post post = Post.builder()
                .member(agent)
                .image("image_url")
                .content("post_content")
                .build();
        postRepository.save(post);

        Alarm alarm = Alarm.builder()
                .type(AlarmType.LIKE_POST)
                .agent(agent)
                .target(target)
                .post(post)
                .build();
        Alarm savedAlarm = alarmRepository.save(alarm);

        Optional<Alarm> foundAlarmBeforeDelete = alarmRepository.findById(savedAlarm.getId());
        Assertions.assertEquals(true, foundAlarmBeforeDelete.isPresent());

        //when
        alarmRepository.deleteByTypeAndAgentAndTargetAndPost(AlarmType.LIKE_POST, agent, target, post);

        //then
        Optional<Alarm> foundAlarmAfterDelete = alarmRepository.findById(savedAlarm.getId());
        Assertions.assertEquals(false, foundAlarmAfterDelete.isPresent());
    }

    @Test
    @DisplayName("[alarm repo] delete by type and agent and target and comment")
    void test_delete_by_type_and_agent_and_target_and_comment() {
        //given
        Member agent = new Member();
        agent.setId(1L);
        Member target = new Member();
        target.setId(2L);
        Comment comment = Comment.builder()
                .writer(agent)
                .text("comment_text")
                .parentsCommentId(null)
                .replyOrder(0)
                .build();

        Alarm alarm = Alarm.builder()
                .type(AlarmType.LIKE_COMMENT)
                .agent(agent)
                .target(target)
                .comment(comment)
                .build();

        Alarm savedAlarm = alarmRepository.save(alarm);
        commentRepository.save(comment);
        Optional<Alarm> foundAlarmBeforeDelete = alarmRepository.findById(savedAlarm.getId());
        Assertions.assertEquals(true, foundAlarmBeforeDelete.isPresent());

        //when
        alarmRepository.deleteByTypeAndAgentAndTargetAndComment(AlarmType.LIKE_COMMENT, agent, target, comment);

        //then
        Optional<Alarm> foundAlarmAfterDelete = alarmRepository.findById(savedAlarm.getId());
        Assertions.assertEquals(false, foundAlarmAfterDelete.isPresent());
    }

    @Test
    @DisplayName("[alarm repo] delete all by comment in list")
    void test_find_all_by_comment_in_list() {
        //given
        Member agent = new Member();
        agent.setId(1L);
        agent.setUsername("luee");
        agent.setName("haneul");
        agent.setPassword("lueepwd1234");
        memberRepository.save(agent);

        Member target = new Member();
        target.setId(2L);
        target.setUsername("luee2");
        target.setName("haneul");
        target.setPassword("lueepwd1234");
        memberRepository.save(target);

        List<Comment> comments = new ArrayList<>();
        List<Alarm> alarms = new ArrayList<>();
        int count = 3;
        for (int c = 0; c < count; c++) {
            Comment comment = Comment.builder()
                    .writer(agent)
                    .text("comment_text" + c)
                    .parentsCommentId(null)
                    .replyOrder(0)
                    .build();
            comments.add(comment);
            commentRepository.save(comment);

            Alarm alarm = Alarm.builder()
                    .type(AlarmType.COMMENT)
                    .agent(agent)
                    .target(target)
                    .comment(comment)
                    .build();
            alarms.add(alarm);
            alarmRepository.save(alarm);
        }

        //when
        List<Alarm> foundAlarms= alarmRepository.findAllByCommentIn(comments);

        //then
        Assertions.assertEquals(count, foundAlarms.size());
        Assertions.assertEquals(agent, foundAlarms.get(0).getAgent());
        Assertions.assertEquals("comment_text1", foundAlarms.get(1).getComment().getText());
    }

    @Test
    @DisplayName("[alarm repo] delete all in batch")
    void test_delete_all_in_batch() {
        //given
        List<Alarm> alarms = new ArrayList<>();
        int count = 3;
        for (int c = 0; c < count; c++) {
            Alarm alarm = new Alarm();
            alarms.add(alarm);
            alarmRepository.save(alarm);
        }
        List<Alarm> foundAlarmsBeforeDelete = alarmRepository.findAll();
        Assertions.assertEquals(count, foundAlarmsBeforeDelete.size());

        //when
        alarmRepository.deleteAllInBatch(alarms);

        //then
        List<Alarm> foundAlarmsAfterDelete = alarmRepository.findAll();
        Assertions.assertEquals(0, foundAlarmsAfterDelete.size());

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