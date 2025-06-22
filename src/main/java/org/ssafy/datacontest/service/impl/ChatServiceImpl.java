package org.ssafy.datacontest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.chatting.ChatMessageRequest;
import org.ssafy.datacontest.dto.chatting.ChatRoomResponse;
import org.ssafy.datacontest.entity.ChatMessage;
import org.ssafy.datacontest.repository.ChatMessageRepository;
import org.ssafy.datacontest.service.ChatService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatServiceImpl(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void saveMessage(ChatMessageRequest request) {
        String roomId = request.getRoomId();
        if (roomId == null || roomId.isBlank()) {
            roomId = createRoomId(request.getSenderEmail(), request.getReceiverEmail());
        }

        ChatMessage message = ChatMessage.builder()
                .roomId(roomId)
                .senderEmail(request.getSenderEmail())
                .receiverEmail(request.getReceiverEmail())
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .read(false)
                .build();

        chatMessageRepository.save(message);
    }

    @Override
    public List<ChatRoomResponse> getChatRoomsByUserEmail(String email) {
        List<ChatMessage> rooms = chatMessageRepository.findBySenderEmailOrReceiverEmail(email, email);

        // Map<roomId, ChatMessage> 최신 메시지만 추출
        Map<String, ChatMessage> latestMap = new HashMap<>();

        for (ChatMessage msg : rooms) {
            ChatMessage prev = latestMap.get(msg.getRoomId());

            if (prev == null || msg.getSentAt().isAfter(prev.getSentAt())) {
                latestMap.put(msg.getRoomId(), msg);
            }
        }

        return latestMap.values().stream()
                .map(msg -> {
                    String partnerEmail = msg.getSenderEmail().equals(email)
                            ? msg.getReceiverEmail()
                            : msg.getSenderEmail();

                    return ChatRoomResponse.builder()
                            .roomId(msg.getRoomId())
                            .lastMessage(msg.getContent())
                            .sentAt(msg.getSentAt())
                            .partnerName(partnerEmail)
                            .read(!msg.isRead() && !msg.getSenderEmail().equals(email))
                            .build();
                })
                .sorted(Comparator.comparing(ChatRoomResponse::getSentAt).reversed())
                .toList();
    }

    @Override
    public List<ChatMessage> getMessagesInRoom(String roomId) {
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    private String createRoomId(String senderEmail, String receiverEmail) {
        return senderEmail.compareTo(receiverEmail) < 0
                ? senderEmail + "_" + receiverEmail
                : receiverEmail + "_" + senderEmail;
    }
}
