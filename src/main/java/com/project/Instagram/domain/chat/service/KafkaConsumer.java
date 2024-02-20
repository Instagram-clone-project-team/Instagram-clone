package com.project.Instagram.domain.chat.service;

import com.project.Instagram.domain.chat.dto.Message;
import com.project.Instagram.global.kafka.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final SimpMessageSendingOperations template;

    /*
    * @KafkaListener으로 구독할 topic과 groupId 설정
    * 그러면 거기서 메시지를 읽어, SimpMessagingTemplate로 STOMP WebSocket으로 메시지를 날림
    * */
    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void consume(Message message) throws IOException {
        log.info("전송 위치 =/topic/tt"+ message.getRoomId());
        log.info("채팅 방으로 메시지 전송 = {}", message);

        // 메시지객체 내부의 채팅방번호를 참조하여, 해당 채팅방 구독자에게 메시지를 발송한다.
        template.convertAndSend("/topic/tt/" + message.getRoomId(), message);
    }
}
