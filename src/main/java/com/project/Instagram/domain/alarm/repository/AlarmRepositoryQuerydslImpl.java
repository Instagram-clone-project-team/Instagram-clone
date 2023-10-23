package com.project.Instagram.domain.alarm.repository;

import com.project.Instagram.domain.alarm.entity.Alarm;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.project.Instagram.domain.alarm.entity.QAlarm.alarm;
import static com.project.Instagram.domain.follow.entity.QFollow.follow;
import static com.project.Instagram.domain.member.entity.QMember.member;
import static com.project.Instagram.domain.post.entity.QPost.post;

@RequiredArgsConstructor
public class AlarmRepositoryQuerydslImpl implements AlarmRepositoryQuerydsl {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Alarm> findAlarmPageByMemberId(Pageable pageable, Long memberId) {
        final List<Alarm> alarmList = jpaQueryFactory
                .selectFrom(alarm)
                .innerJoin(alarm.agent, member).fetchJoin()
                .leftJoin(alarm.post, post).fetchJoin()
                .leftJoin(alarm.follow, follow)
                .where(alarm.target.id.eq(memberId))
                .orderBy(alarm.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final long total = jpaQueryFactory
                .selectFrom(alarm)
                .where(alarm.target.id.eq(memberId))
                .fetchCount();

        return new PageImpl<>(alarmList, pageable, total);
    }
}
