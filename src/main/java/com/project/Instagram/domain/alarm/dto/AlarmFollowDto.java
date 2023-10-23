package com.project.Instagram.domain.alarm.dto;

import com.project.Instagram.domain.alarm.entity.Alarm;
import com.project.Instagram.domain.member.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmFollowDto extends AlarmDto {
    private boolean isFollowing;

    public AlarmFollowDto(Alarm alarm, boolean isFollowing) {
        super(alarm.getId(), alarm.getType().name(), alarm.getType().getMessage(), new Profile(alarm.getAgent()));
        this.isFollowing = isFollowing;
    }
}
