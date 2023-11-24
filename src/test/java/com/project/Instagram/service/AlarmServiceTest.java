package com.project.Instagram.service;

import com.project.Instagram.domain.alarm.dto.AlarmDto;
import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.entity.Alarm;
import com.project.Instagram.domain.alarm.repository.AlarmRepository;
import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.domain.follow.repository.FollowRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.global.util.SecurityUtil;
import com.project.Instagram.global.util.StringExtractUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

import static com.project.Instagram.domain.alarm.dto.AlarmType.LIKE_POST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlarmServiceTest {
    @InjectMocks
    private AlarmService alarmService;
    @Mock
    private AlarmRepository alarmRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private FollowRepository followRepository;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private StringExtractUtil stringExtractUtil;

    //팔로우,게시글좋아요,게시글멘션,댓글,댓글좋아요,댓글멘션
    @Nested
    class getArlamsOnLike{
        @Test
        @DisplayName(" 게시글 좋아요 알람 가져오기 테스트")
        void validGetArlamsOnPostLike(){
            Member loginMember = Member.builder()
                    .name("사사사")
                    .username("exex22")
                    .password("qwer1234").build();
            loginMember.setId(1L);
            Post post = Post.builder().build();
            post.setId(1L);

            Alarm alarm = Alarm.builder()
                    .post(post)
                    .agent(loginMember)
                    .type(LIKE_POST).build();
            List<Alarm> alarms = new ArrayList<>();
            int page = 0;
            int size = 6;
            alarms.add(alarm);

            when(securityUtil.getLoginMember()).thenReturn(loginMember);
            Pageable pageable = PageRequest.of(page, size);
            Page<Alarm> response = new PageImpl<>(alarms);
            when(alarmRepository.findByTargetId(loginMember.getId(),pageable)).thenReturn(response);

            //when
            Page<AlarmDto> responseDto = alarmService.getAlarms(page,size);

            //then
            List<AlarmDto> testDto =responseDto.getContent();
            assertEquals(testDto,response);
        }
    }
    @Nested
    class getArlamsOnMention{

    }
    @Nested
    class getArlamsOncComment{

    }
    @Nested
    class getArlamsOnFollow{

    }
}
