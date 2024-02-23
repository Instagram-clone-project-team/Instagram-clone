package com.project.Instagram.domain.chat.controller;

import com.project.Instagram.domain.chat.dto.ChatRoomCreateResponse;
import com.project.Instagram.domain.chat.dto.MessageRequest;
import com.project.Instagram.domain.chat.service.ChatService;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;

@Validated
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat/rooms")
    public ResponseEntity<ResultResponse> createChatRoom(
            @RequestParam List<@NotEmpty @Size(max = 12) String> usernames) {
        final ChatRoomCreateResponse response = chatService.createRoom(usernames);

        return ResponseEntity.ok(ResultResponse.of(CREATE_CHAT_ROOM_SUCCESS, response));
    }

    @MessageMapping("/messages")
    public void sendMessage(@Valid @RequestBody MessageRequest request) {
        chatService.sendMessage(request);
    }
}
