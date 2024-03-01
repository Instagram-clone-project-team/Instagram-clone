package com.project.Instagram.domain.chat.service;

import com.project.Instagram.domain.chat.dto.*;
import com.project.Instagram.domain.chat.entity.ChatRoom;
import com.project.Instagram.domain.chat.entity.Message;
import com.project.Instagram.domain.chat.entity.Room;
import com.project.Instagram.domain.chat.entity.RoomMember;
import com.project.Instagram.domain.chat.repository.*;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.member.repository.MemberRepository;
import com.project.Instagram.global.entity.PageListResponse;
import com.project.Instagram.global.error.BusinessException;
import com.project.Instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.Instagram.domain.member.entity.Profile.convertMemberToProfile;
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
    private final SimpMessagingTemplate messagingTemplate;

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
            throw new BusinessException(CHAT_ROOM_NOT_FOUND);

        final Message message = messageRepository.save(new Message(request.getContent(), sender, room));
        updateRoom(request.getSenderId(), room, roomMembers, message);

        roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername()));
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

    public PageListResponse<MessageDto> getChatMessages(Long roomId, int page, int size) {
        final Long memberId = securityUtil.getLoginMember().getId();
        final Pageable pageable = PageRequest.of(page, size);

        final ChatRoom chatRoom = chatRoomRepository.findByMemberIdAndRoomId(memberId, roomId)
                .orElseThrow(() -> new BusinessException(MESSAGE_NOT_FOUND));

        final Page<Message> messagePage = messageRepository.findByRoomId(chatRoom.getRoom().getId(), pageable);

        return getMessageResponsePage(messagePage);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
        final Message message = messageRepository.findById(messageId).orElseThrow(() -> new BusinessException(MESSAGE_NOT_FOUNT));

        if (!message.getMember().getId().equals(member.getId())) {
            throw new BusinessException(MISS_MATCH);
        }

        final Room room = message.getRoom();
        final List<ChatRoom> joinRooms = chatRoomRepository.findByRoomId(room.getId());
        joinRooms.forEach(joinRoom -> {
            final LocalDateTime createdDateOfMessageToDelete = message.getCreatedDate();
            final LocalDateTime createdDateOfJoinRoom = joinRoom.getCreatedDate();

            if (!createdDateOfMessageToDelete.isBefore(createdDateOfJoinRoom)) {
                if (message.equals(joinRoom.getMessage())) {
                    final LocalDateTime start = createdDateOfJoinRoom.minusSeconds(1L);
                    final LocalDateTime end = createdDateOfMessageToDelete.plusSeconds(1L);
                    final Long total = messageRepository.countByCreatedDateBetweenAndRoom(start, end, room);

                    if (total == 1) {
                        chatRoomRepository.delete(joinRoom);
                    } else {
                        final List<Message> messages = messageRepository.findTop2ByCreatedDateBetweenAndRoomOrderByIdDesc(
                                start, end, room);
                        joinRoom.updateMessage(messages.get(1));
                    }
                }
            }
        });

        messageRepository.delete(message);

        final List<RoomMember> roomMembers = roomMemberRepository.findAllByRoom(room);
        roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getMember().getUsername()));
    }

    @Transactional
    public boolean deleteChatRoom(Long roomId) {
        final Room room = roomRepository.findById(roomId).orElseThrow(() -> new BusinessException(CHAT_ROOM_NOT_FOUND));
        final Member loginMember = securityUtil.getLoginMember();

        if (chatRoomRepository.findByMemberAndRoom(loginMember, room).isEmpty()) {
            throw new BusinessException(CHAT_ROOM_NOT_FOUND);
        }

        chatRoomRepository.deleteByMemberAndRoom(loginMember, room);

        return true;
    }

    public PageListResponse<ChatRoomDto> getChatRooms(int page, int size) {
        final Member loginMember = securityUtil.getLoginMember();
        final Pageable pageable = PageRequest.of(page, size);

        final Page<ChatRoom> chatRoomPage = chatRoomRepository.findByMemberId(loginMember.getId(), pageable);

        List<ChatRoomDto> chatRoomResponses = new ArrayList<>();
        List<ChatRoom> chatRooms = chatRoomPage.getContent();
        for (ChatRoom chatRoom : chatRooms) {
            List<Message> messages = messageRepository.findByRoomId(chatRoom.getRoom().getId());
            String lastMessage = messages.get(0).getContent();

            chatRoomResponses.add(new ChatRoomDto(chatRoom.getRoom().getId()
                    , lastMessage
                    , loginMember, chatRoom.getRoom().getRoomMembers()));

        }
        return new PageListResponse<>(chatRoomResponses, chatRoomPage);
    }

    private void updateRoom(Long senderId, Room room, List<RoomMember> roomMembers, Message message) {
        final List<Member> members = roomMembers.stream()
                .map(RoomMember::getMember)
                .collect(Collectors.toList());
        final Map<Long, ChatRoom> chatRoomMap = chatRoomRepository.findByRoomAndMemberIn(room, members).stream()
                .collect(Collectors.toMap(j -> j.getMember().getId(), j -> j));

        final List<ChatRoom> newChatRooms = new ArrayList<>();
        final List<ChatRoom> updateChatRooms = new ArrayList<>();

        for (final RoomMember roomMember : roomMembers) {
            final Member member = roomMember.getMember();

            if (chatRoomMap.containsKey(member.getId())) {
                updateChatRooms.add(chatRoomMap.get(member.getId()));
            } else {
                newChatRooms.add(new ChatRoom(room, member, message));
            }
        }

        chatRoomRepository.saveAllBatch(newChatRooms, message);
        chatRoomRepository.updateAllBatch(updateChatRooms, message);
    }

    private PageListResponse<MessageDto> getMessageResponsePage(Page<Message> messagePage) {
        List<Message> messages = messagePage.getContent();
        List<MessageDto> messageResponses = new ArrayList<>();
        for (Message message : messages) {
            messageResponses.add(new MessageDto(message, convertMemberToProfile(message.getMember().getUsername(), message.getMember().getImage())));
        }
        PageListResponse<MessageDto> postResponsePage = new PageListResponse<>(messageResponses, messagePage);
        return postResponsePage;
    }
}
