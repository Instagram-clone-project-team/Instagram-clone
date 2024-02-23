package com.project.Instagram.domain.chat.repository;

import com.project.Instagram.domain.chat.entity.RoomMember;
import com.project.Instagram.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> ,RoomMemberRepositoryJdbc{
    List<RoomMember> findAllByMemberIn(List<Member> members);

    List<RoomMember> findAllByRoomIdIn(List<Long> roomIds);

    @Query("select rm from RoomMember rm join fetch rm.member where rm.room.id = :roomId")
    List<RoomMember> findAllWithMemberByRoomId(@Param("roomId") Long roomId);
}
