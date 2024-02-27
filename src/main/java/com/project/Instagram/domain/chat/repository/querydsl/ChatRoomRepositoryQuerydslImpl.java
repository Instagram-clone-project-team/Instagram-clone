package com.project.Instagram.domain.chat.repository.querydsl;

import com.project.Instagram.domain.chat.entity.ChatRoom;
import com.project.Instagram.domain.chat.repository.querydsl.ChatRoomRepositoryQuerydsl;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.project.Instagram.domain.chat.entity.QChatRoom.chatRoom;
import static com.project.Instagram.domain.chat.entity.QRoom.room;
import static com.project.Instagram.domain.member.entity.QMember.member;

@RequiredArgsConstructor
public class ChatRoomRepositoryQuerydslImpl implements ChatRoomRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ChatRoom> findByMemberIdAndRoomId(Long memberId, Long roomId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(chatRoom)
                .where(chatRoom.member.id.eq(memberId).and(chatRoom.room.id.eq(roomId)))
                .innerJoin(chatRoom.room, room).fetchJoin()
                .innerJoin(chatRoom.member, member).fetchJoin()
                .fetchOne());
    }

    @Override
    public Page<ChatRoom> findByMemberId(Long memberId, Pageable pageable) {
        JPQLQuery<ChatRoom> query = queryFactory.select(chatRoom).from(chatRoom).where(chatRoom.member.id.eq(memberId));
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<ChatRoom> result = query.fetch();
        long total = query.fetchCount();

        return new PageImpl<>(result, pageable, total);
    }

}
