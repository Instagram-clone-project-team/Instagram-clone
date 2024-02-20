package com.project.Instagram.global.kafka;

import com.project.Instagram.domain.chat.service.ChatRoomService;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 우선 순위를 높게 설정해서 SecurityFilter들 보다 앞서 실행되게 해준다.
@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final SecurityUtil securityUtil;
    private ChatRoomService chatRoomService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String email = securityUtil.getLoginMember().getEmail();
        log.info("StompAccessor = {}", accessor);
        if(accessor.getCommand() == StompCommand.CONNECT){
            Long chatRoomId = Long.valueOf(
                    Objects.requireNonNull(
                            accessor.getFirstNativeHeader("chatRoomId")
                    )
            );
            chatRoomService.connectChatRoom(chatRoomId, email);
            //메시지 보내기
        }
        else{
            securityUtil.checkLoginMember();
        }
        return message;
    }

}
