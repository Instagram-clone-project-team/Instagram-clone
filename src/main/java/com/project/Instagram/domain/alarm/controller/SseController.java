package com.project.Instagram.domain.alarm.controller;

import com.project.Instagram.domain.alarm.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseController {
    private final SseService sseService;

    @GetMapping("/connection")
    public ResponseEntity<SseEmitter> sseConnection(@RequestParam String username) {
        return new ResponseEntity<>(sseService.connect(username), HttpStatus.OK);
    }
}
