package com.project.Instagram.domain.alarm.dto;

import com.project.Instagram.domain.member.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDto {
    private Long id;
    private String type;
    private String message;
    private Profile agent;
}
