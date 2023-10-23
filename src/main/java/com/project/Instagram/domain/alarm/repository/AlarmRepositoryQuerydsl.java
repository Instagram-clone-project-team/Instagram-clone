package com.project.Instagram.domain.alarm.repository;

import com.project.Instagram.domain.alarm.entity.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlarmRepositoryQuerydsl {

    Page<Alarm> findAlarmPageByMemberId(Pageable pageable, Long memberId);
}
