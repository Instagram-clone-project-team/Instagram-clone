package com.project.Instagram.domain.chat.repository.jdbc;

import com.project.Instagram.domain.chat.entity.Room;
import com.project.Instagram.domain.member.entity.Member;

import java.util.List;

public interface RoomMemberRepositoryJdbc {

    void saveAllBatch(Room room, List<Member> members);
}
