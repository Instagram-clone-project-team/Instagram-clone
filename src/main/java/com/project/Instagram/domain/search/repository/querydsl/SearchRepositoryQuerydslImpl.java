package com.project.Instagram.domain.search.repository.querydsl;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.search.dto.QSearchHashtagDto;
import com.project.Instagram.domain.search.dto.QSearchMemberDto;
import com.project.Instagram.domain.search.dto.SearchHashtagDto;
import com.project.Instagram.domain.search.dto.SearchMemberDto;
import com.project.Instagram.domain.search.entity.Search;
import com.project.Instagram.domain.search.repository.querydsl.SearchRepositoryQuerydsl;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.project.Instagram.domain.follow.entity.QFollow.follow;
import static com.project.Instagram.domain.search.entity.QSearch.search;
import static com.project.Instagram.domain.search.entity.QSearchHashtag.searchHashtag;
import static com.project.Instagram.domain.search.entity.QSearchMember.searchMember;


@RequiredArgsConstructor
public class SearchRepositoryQuerydslImpl implements SearchRepositoryQuerydsl {
    private final int SEARCH_SIZE = 15;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Search> findAllByTextLike(String text) {
        final String keyword = text + "%";

        return jpaQueryFactory
                .select(search)
                .from(search)
                .where(search.id.in(
                                JPAExpressions
                                        .select(searchMember.id)
                                        .from(searchMember)
                                        .where(searchMember.member.username.like(keyword)
                                                .or(searchMember.member.name.like(keyword))))
                        .or(search.id.in(
                                JPAExpressions
                                        .select(searchHashtag.id)
                                        .from(searchHashtag)
                                        .where(searchHashtag.hashtag.tagName.like(keyword)))))
                .orderBy(search.count.desc())
                .limit(50)
                .fetch();
    }

    @Override
    public List<Member> findMembersByText(String text) {
        return jpaQueryFactory
                .select(searchMember.member)
                .from(searchMember)
                .where(searchMember.member.username.contains(text))
                .orderBy(searchMember.count.desc())
                .limit(SEARCH_SIZE)
                .fetch();
    }

    @Override
    public List<Hashtag> findHashTagsByText(String text) {
        final String keyword = text + '%';
        return jpaQueryFactory
                .select(searchHashtag.hashtag)
                .from(searchHashtag)
                .where(searchHashtag.hashtag.tagName.like(keyword))
                .orderBy(searchHashtag.count.desc())
                .limit(SEARCH_SIZE)
                .fetch();
    }

    @Override
    public void checkMatchingHashtag(String text, List<Search> searches, List<Long> searchIds) {
        final Search matchingSearch = jpaQueryFactory
                .select(searchHashtag._super)
                .from(searchHashtag)
                .where(searchHashtag.hashtag.tagName.eq(text))
                .fetchOne();

        if (matchingSearch != null && !searchIds.contains(matchingSearch.getId())) {
            searches.add(0, matchingSearch);
            searchIds.add(0, matchingSearch.getId());
            checkSearchSize(searches);
            checkSearchSize(searchIds);
        }
    }

    @Override
    public void checkMatchingMember(String text, List<Search> searches, List<Long> searchIds) {
        final Search matchingSearch = jpaQueryFactory
                .select(searchMember._super)
                .from(searchMember)
                .where(searchMember.member.username.eq(text))
                .fetchOne();

        if (matchingSearch != null && !searchIds.contains(matchingSearch.getId())) {
            searches.add(0, matchingSearch);
            searchIds.add(0, matchingSearch.getId());
            checkSearchSize(searches);
            checkSearchSize(searchIds);
        }
    }

    @Override
    public Map<Long, SearchMemberDto> findAllSearchMemberDtoByIdIn(Long loginId, List<Long> searchIds) {
        return jpaQueryFactory
                .from(searchMember)
                .where(searchMember.id.in(searchIds))
                .transform(GroupBy.groupBy(searchMember.id).as(new QSearchMemberDto(
                        searchMember._super.dtype,
                        searchMember.member,
                        JPAExpressions
                                .selectFrom(follow)
                                .where(follow.member.id.eq(loginId).and(follow.followMember.id.eq(searchMember.member.id)))
                                .exists(),
                        JPAExpressions
                                .selectFrom(follow)
                                .where(follow.member.id.eq(searchMember.member.id).and(follow.followMember.id.eq(loginId)))
                                .exists())));
    }

    @Override
    public Map<Long, SearchHashtagDto> findAllSearchHashtagDtoByIdIn(List<Long> searchIds) {
        return jpaQueryFactory
                .from(searchHashtag)
                .where(searchHashtag.id.in(searchIds))
                .transform(GroupBy.groupBy(searchHashtag.id).as(new QSearchHashtagDto(
                        searchHashtag.dtype,
                        searchHashtag.hashtag)));
    }

    private <T> void checkSearchSize(List<T> list) {
        while (list.size() > 50) {
            list.remove(list.size() - 1);
        }
    }
}
