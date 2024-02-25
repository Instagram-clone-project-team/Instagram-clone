package com.project.Instagram.domain.chat.controller;

import com.project.Instagram.domain.chat.dto.ChatRoomCreateResponse;
import com.project.Instagram.domain.chat.dto.MessageDto;
import com.project.Instagram.domain.chat.dto.MessageRequest;
import com.project.Instagram.domain.chat.dto.MessageSimpleRequest;
import com.project.Instagram.domain.chat.service.ChatService;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

import static com.project.Instagram.global.response.ResultCode.*;

@Validated
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat/rooms")
    public ResponseEntity<ResultResponse> createChatRoom(@RequestParam List<@NotEmpty @Size(max = 12) String> usernames) {
        final ChatRoomCreateResponse response = chatService.createRoom(usernames);

        return ResponseEntity.ok(ResultResponse.of(CREATE_CHAT_ROOM_SUCCESS, response));
    }

    @GetMapping("/chat/rooms/messages/{roomId}")
    public ResponseEntity<ResultResponse> getChatMessages(@PathVariable Long roomId, @Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                          @Positive @RequestParam(value = "size", defaultValue = "5") int size) {

        final PageListResponse<MessageDto> response = chatService.getChatMessages(roomId, page -1, size);

        return ResponseEntity.ok(ResultResponse.of(GET_CHAT_MESSAGES_SUCCESS, response));
    }

    @MessageMapping("/messages")
    public void sendMessage(@Valid @RequestBody MessageRequest request) {
        chatService.sendMessage(request);
    }

    @MessageMapping("/messages/delete")
    public void deleteMessage(@Valid @RequestBody MessageSimpleRequest request) {
        chatService.deleteMessage(request.getMessageId(), request.getMemberId());
    }
}
