package com.project.Instagram.domain.chat.service;

import com.project.Instagram.domain.chat.dto.ChatRoomCreateResponse;
import com.project.Instagram.domain.chat.dto.MemberSimpleInfo;
import com.project.Instagram.domain.chat.entity.Room;
import com.project.Instagram.domain.chat.entity.RoomMember;
import com.project.Instagram.domain.chat.repository.RoomMemberRepository;
import com.project.Instagram.domain.chat.repository.RoomRepository;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final SecurityUtil securityUtil;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    @Transactional
    public ChatRoomCreateResponse createRoom(List<String> usernames) {
        final Member inviter = securityUtil.getLoginMember();
        usernames.add(inviter.getUsername());
        final List<Member> members = memberRepository.findAllByUsernameIn(usernames);

        final Room room;
        final boolean status;
        final Optional<Room> roomOptional = getRoomByMembers(members);
        if (roomOptional.isEmpty()) {
            status = true;
            room = roomRepository.save(new Room(inviter));
            roomMemberRepository.saveAllBatch(room, members);
        } else {
            status = false;
            room = roomOptional.get();
        }

        final List<MemberSimpleInfo> memberSimpleInfos = members.stream()
                .map(MemberSimpleInfo::new)
                .collect(Collectors.toList());

        return new ChatRoomCreateResponse(room.getId(), new MemberSimpleInfo(inviter), memberSimpleInfos);
    }

    private Optional<Room> getRoomByMembers(List<Member> members) {
        final Map<Long, List<RoomMember>> roomMembersMap = roomMemberRepository.findAllByMemberIn(members)
                .stream()
                .collect(Collectors.groupingBy(r -> r.getRoom().getId()));

        final List<Long> roomIds = new ArrayList<>();
        roomMembersMap.forEach((rid, rms) -> {
            if (rms.size() == members.size()) {
                roomIds.add(rid);
            }
        });
        final Map<Long, List<RoomMember>> roomMemberMapGroupByRoomId = roomMemberRepository.findAllByRoomIdIn(roomIds)
                .stream()
                .collect(Collectors.groupingBy(r -> r.getRoom().getId()));

        for (final Long roomId : roomMemberMapGroupByRoomId.keySet()) {
            if (roomMemberMapGroupByRoomId.get(roomId).size() == members.size()) {
                return Optional.of(roomMemberMapGroupByRoomId.get(roomId).get(0).getRoom());
            }
        }

        return Optional.empty();
    }
}
