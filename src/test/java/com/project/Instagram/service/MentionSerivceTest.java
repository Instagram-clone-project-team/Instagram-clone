package com.project.Instagram.service;

import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.mention.service.MentionService;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.global.util.StringExtractUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentionSerivceTest {

    @InjectMocks
    private MentionService mentionService;
    @Mock
    private AlarmService alarmService;
    @Mock
    private StringExtractUtil stringExtractUtil;
    @Mock
    private MemberRepository memberRepository;
    @Nested
    class checkMentionsFromComment{
        @Test
        @DisplayName("댓글 멘션 동작 테스트")
        void sendAlram(){
            //given
            Member agent = Member.builder()
                    .username("exex11")
                    .build();
            Post post = new Post();
            Comment comment = new Comment();
            comment.updateText("@exex22 @e2e222");
            List<String> mentionTargets = Arrays.asList("exex22","e2e222");
            when(stringExtractUtil.filteringMentions(comment.getText())).thenReturn(mentionTargets);

            //when
            mentionService.checkMentionsFromComment(agent, comment.getText(), post,comment);

            //then
           verify(alarmService,times(1)).sendMentionCommentAlarm(
                   eq(AlarmType.MENTION_COMMENT),
                   eq(agent),
                   eq(List.of("exex22","e2e222")),
                   eq(post),
                   eq(comment)
           );
        }
    }
}
