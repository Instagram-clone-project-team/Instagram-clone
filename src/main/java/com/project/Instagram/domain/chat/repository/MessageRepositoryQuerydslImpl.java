package com.project.Instagram.domain.chat.repository;

import com.project.Instagram.domain.chat.entity.ChatRoom;
import com.project.Instagram.domain.chat.entity.Message;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.project.Instagram.domain.chat.entity.QMessage.message;


@RequiredArgsConstructor
public class MessageRepositoryQuerydslImpl implements MessageRepositoryQuerydsl{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Message> findAllByChatRoom(ChatRoom chatRoom, Pageable pageable) {
        final List<Message> messages = queryFactory
                .selectFrom(message)
                .where(isMessageByRoomIdAndAfter(chatRoom.getRoom().getId(), chatRoom.getCreatedDate()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(message.id.desc())
                .fetch();

        final long total = queryFactory
                .selectFrom(message)
                .where(isMessageByRoomIdAndAfter(chatRoom.getRoom().getId(), chatRoom.getCreatedDate()))
                .fetchCount();

        return new PageImpl<>(messages, pageable, total);
    }

    private BooleanExpression isMessageByRoomIdAndAfter(Long roomId, LocalDateTime chatDate) {
        return message.room.id.eq(roomId).and(message.createdDate.goe(chatDate));
    }
}
