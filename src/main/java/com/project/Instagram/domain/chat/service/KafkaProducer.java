package com.project.Instagram.domain.chat.service;

import com.project.Instagram.domain.chat.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

// producerConfig에서 등록한 카프카템플릿을 이용해 지정해 놓은 topic에 메시지를 보내는 방식
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, Message> kafkaTemplate;

    public void send(String topic, Message messageDto) {
        log.info("topic : " + topic);
        log.info("send Message : " + messageDto);
        kafkaTemplate.send(topic, messageDto);
    }
}
