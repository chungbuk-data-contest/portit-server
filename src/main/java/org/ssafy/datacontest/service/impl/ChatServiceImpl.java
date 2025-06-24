package org.ssafy.datacontest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.chatting.ChatMessageRequest;
import org.ssafy.datacontest.dto.chatting.ChatMessageResponse;
import org.ssafy.datacontest.entity.ChatRoom;
import org.ssafy.datacontest.entity.mongo.ChatMessage;
import org.ssafy.datacontest.repository.ChatMessageRepository;
import org.ssafy.datacontest.repository.ChatRoomRepository;
import org.ssafy.datacontest.service.ChatService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatServiceImpl(ChatMessageRepository chatMessageRepository,
                           ChatRoomRepository chatRoomRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public void saveMessage(Long roomId, ChatMessageRequest request) {
        ChatMessage message = ChatMessage.builder()
                .roomId(roomId)
                .sender(request.getSender())
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .read(false)
                .build();

        chatMessageRepository.save(message);
    }

    @Override
    public List<ChatMessageResponse> getChatRoomsByUserEmail(String email) {
        List<ChatMessage> rooms = chatMessageRepository.findBySender(email); // 이 부분도 리팩터링 필요

        // 메시지 목록 기준 roomId별 최신 메시지 뽑기
        Map<Long, ChatMessage> latestMap = new HashMap<>();

        for (ChatMessage msg : rooms) {
            ChatMessage prev = latestMap.get(msg.getRoomId());

            if (prev == null || msg.getSentAt().isAfter(prev.getSentAt())) {
                latestMap.put(msg.getRoomId(), msg);
            }
        }

        // ChatRoom id 모아서 조회
        List<Long> roomIds = latestMap.values().stream()
                .map(ChatMessage::getRoomId)
                .toList();

        Map<Long, ChatRoom> chatRoomMap = chatRoomRepository.findAllById(roomIds).stream()
                .collect(Collectors.toMap(ChatRoom::getId, cr -> cr));

        // 각 메시지에 대해 상대방 이름 추론
        return latestMap.values().stream()
                .map(msg -> {
                    ChatRoom room = chatRoomMap.get(Long.valueOf(msg.getRoomId()));
                    String partnerEmail = room.getUser().getLoginId().equals(email)
                            ? room.getCompany().getLoginId()
                            : room.getUser().getLoginId();

                    return ChatMessageResponse.builder()
                            .roomId(msg.getRoomId())
                            .lastMessage(msg.getContent())
                            .sentAt(msg.getSentAt())
                            .partnerName(partnerEmail)
                            .read(!msg.isRead() && !msg.getSender().equals(email))
                            .build();
                })
                .sorted(Comparator.comparing(ChatMessageResponse::getSentAt).reversed())
                .toList();
    }

    @Override
    public List<ChatMessage> getMessagesInRoom(Long roomId) {
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    private String createRoomId(String senderEmail, String receiverEmail) {
        return senderEmail.compareTo(receiverEmail) < 0
                ? senderEmail + "_" + receiverEmail
                : receiverEmail + "_" + senderEmail;
    }
}
