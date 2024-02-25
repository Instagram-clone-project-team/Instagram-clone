package com.project.Instagram.domain.chat.repository;

import com.project.Instagram.domain.chat.entity.Message;
import com.project.Instagram.domain.chat.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryQuerydsl{

    Optional<Message> findById(Long id);

    Long countByCreatedDateBetweenAndRoom(LocalDateTime start, LocalDateTime end, Room room);

    List<Message> findTop2ByCreatedDateBetweenAndRoomOrderByIdDesc(LocalDateTime start, LocalDateTime end, Room room);
}
