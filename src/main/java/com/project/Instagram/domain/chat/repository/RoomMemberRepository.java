package com.project.Instagram.domain.chat.repository;

import com.project.Instagram.domain.chat.entity.RoomMember;
import com.project.Instagram.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RoomMemberRepository extends JpaRepository<RoomMember, Long>, RoomMemberRepositoryJdbc {

    List<RoomMember> findAllByMemberIn(List<Member> members);

    List<RoomMember> findAllByRoomIdIn(List<Long> roomIds);
}
