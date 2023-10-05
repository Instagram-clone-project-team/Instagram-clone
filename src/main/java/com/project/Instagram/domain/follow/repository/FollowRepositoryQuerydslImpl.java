package com.project.Instagram.domain.follow.repository;

import static com.project.Instagram.domain.member.entity.QMember.*;
import static com.project.Instagram.domain.follow.entity.QFollow.*;

import com.project.Instagram.domain.follow.dto.FollowerDto;
import com.project.Instagram.domain.follow.dto.QFollowerDto;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class FollowRepositoryQuerydslImpl implements FollowRepositoryQuerydsl {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<FollowerDto> findFollowings(Long loginId, Long memberId, Pageable pageable) {
        JPQLQuery<FollowerDto> query = jpaQueryFactory
                .select(new QFollowerDto(
                        member,
                        JPAExpressions
                                .selectFrom(follow)
                                .where(follow.member.id.eq(loginId).and(follow.followMember.eq(member)))
                                .exists(),
                        JPAExpressions
                                .selectFrom(follow)
                                .where(follow.member.eq(member).and(follow.followMember.id.eq(loginId)))
                                .exists(),
                        member.id.eq(loginId)))
                .from(member)
                .where(member.id.in(findFollowingIdList(memberId)));

        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<FollowerDto> result = query.fetch();
        return new PageImpl<>(result, pageable, result.size());
    }

    private JPQLQuery<Long> findFollowingIdList(Long memberId) {
        return JPAExpressions
                .select(follow.followMember.id)
                .from(follow)
                .where(follow.member.id.eq(memberId));
    }
}
