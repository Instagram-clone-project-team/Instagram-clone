package com.project.Instagram.domain.chat.repository;

import com.project.Instagram.domain.chat.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long>  {
}
