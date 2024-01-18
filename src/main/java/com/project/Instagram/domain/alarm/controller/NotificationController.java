package com.project.Instagram.domain.alarm.controller;

import com.project.Instagram.domain.alarm.service.NotificationService;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.project.Instagram.global.response.ResultCode.ALARMS_CONNECT_SUCCESS;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

//    @GetMapping("/subscribe/{username}")
//    public ResponseEntity<ResultResponse> subscribe(@PathVariable("username") String username) {
//        return ResponseEntity.ok(ResultResponse.of(ALARMS_CONNECT_SUCCESS, notificationService.connectNotification(username)));
//    }

    @GetMapping("/subscribe/{username}")
    public ResponseEntity<SseEmitter> subscribe(@PathVariable("username") String username) {
        return new ResponseEntity<>(notificationService.connectNotification(username), HttpStatus.OK);
    }
}
