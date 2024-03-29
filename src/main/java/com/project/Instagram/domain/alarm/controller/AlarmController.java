package com.project.Instagram.domain.alarm.controller;

import com.project.Instagram.domain.alarm.dto.AlarmDto;
import com.project.Instagram.domain.alarm.service.AlarmService;
import com.project.Instagram.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.Positive;

import static com.project.Instagram.global.response.ResultCode.GET_ALARMS_SUCCESS;

@Validated
@RestController
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;

    @GetMapping("/alarms")
    public ResponseEntity<ResultResponse> getAlarmsPage(
            @Positive @RequestParam(value = "page", defaultValue = "1") int page,
            @Positive @RequestParam(value = "size", defaultValue = "5") int size) {
        final Page<AlarmDto> alarmResponse = alarmService.getAlarms(page - 1, size);
        return ResponseEntity.ok(ResultResponse.of(GET_ALARMS_SUCCESS, alarmResponse));
    }

    @GetMapping("/subscribe/{username}")
    public ResponseEntity<SseEmitter> subscribe(@PathVariable("username") String username) {
        return new ResponseEntity<>(alarmService.connectSubscribe(username), HttpStatus.OK);
    }
}


