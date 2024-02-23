package com.project.Instagram.domain.chat.service;

import com.project.Instagram.domain.chat.dto.ChatRoomCreateResponse;
import com.project.Instagram.domain.chat.dto.MemberSimpleInfo;
import com.project.Instagram.domain.chat.dto.MessageRequest;
import com.project.Instagram.domain.chat.entity.ChatRoom;
import com.project.Instagram.domain.chat.entity.Message;
import com.project.Instagram.domain.chat.entity.Room;
import com.project.Instagram.domain.chat.entity.RoomMember;
import com.project.Instagram.domain.chat.repository.*;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.Instagram.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final SecurityUtil securityUtil;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

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

        return new ChatRoomCreateResponse(status, room.getId(), new MemberSimpleInfo(inviter), memberSimpleInfos);
    }

    @Transactional
    public void sendMessage(MessageRequest request) {
        final Member sender = memberRepository.findById(request.getSenderId())
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
        final Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> new BusinessException(CHAT_ROOM_NOT_FOUND));
        final List<RoomMember> roomMembers = roomMemberRepository.findAllWithMemberByRoomId(room.getId());
        if (roomMembers.stream().noneMatch(r -> r.getMember().getId().equals(sender.getId())))
            throw new BusinessException(JOIN_ROOM_NOT_FOUND);

        final Message message = messageRepository.save(new Message(request.getContent(), sender, room));
        updateRoom(request.getSenderId(), room, roomMembers, message);

//        final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_GET, new MessageDto(message));
//        roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername(), response));
    }

    private void updateRoom(Long senderId, Room room, List<RoomMember> roomMembers, Message message) {
        final List<Member> members = roomMembers.stream()
                .map(RoomMember::getMember)
                .collect(Collectors.toList());
        final Map<Long, ChatRoom> joinRoomMap = chatRoomRepository.findByRoomAndMemberIn(room, members).stream()
                .collect(Collectors.toMap(j -> j.getMember().getId(), j -> j));

        final List<ChatRoom> newJoinRooms = new ArrayList<>();
        final List<ChatRoom> updateJoinRooms = new ArrayList<>();

//        for (final RoomMember roomMember : roomMembers) {
//            final Member member = roomMember.getMember();
//            if (!member.getId().equals(senderId)) {
//                newRoomUnreadMembers.add(new RoomUnreadMember(room, message, member));
//            }
//            if (joinRoomMap.containsKey(member.getId())) {
//                updateJoinRooms.add(joinRoomMap.get(member.getId()));
//            } else {
//                newJoinRooms.add(new JoinRoom(room, member, message));
//            }
//        }
//
//        roomUnreadMemberRepository.saveAllBatch(newRoomUnreadMembers, message);
//        joinRoomRepository.saveAllBatch(newJoinRooms, message);
//        joinRoomRepository.updateAllBatch(updateJoinRooms, message);
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
