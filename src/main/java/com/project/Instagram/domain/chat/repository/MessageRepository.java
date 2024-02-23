package com.project.Instagram.domain.chat.repository;

import com.project.Instagram.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
