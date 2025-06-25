package org.ssafy.datacontest.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.chatting.ChatMessageRequest;
import org.ssafy.datacontest.dto.chatting.ChatMessageResponse;
import org.ssafy.datacontest.entity.ChatRoom;
import org.ssafy.datacontest.entity.mongo.ChatMessage;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.repository.ChatMessageRepository;
import org.ssafy.datacontest.repository.ChatRoomRepository;
import org.ssafy.datacontest.service.ChatService;

import java.time.LocalDateTime;

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
    @Transactional
    public ChatMessageResponse saveMessage(Long roomId, ChatMessageRequest request, String loginId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.CHATROOM_NOT_FOUND));

        // MongoDB에 저장할 sender: loginId
        ChatMessage message = ChatMessage.builder()
                .roomId(roomId)
                .sender(loginId)
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .read(false)
                .build();

        chatMessageRepository.save(message);

        // 보여줄 이름 (닉네임 or 회사명)
        String senderName;
        if (room.getUser().getLoginId().equals(loginId)) {
            senderName = room.getUser().getLoginId();
        } else if (room.getCompany().getLoginId().equals(loginId)) {
            senderName = room.getCompany().getLoginId();
        } else {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_USER);
        }

        return ChatMessageResponse.builder()
                .sender(senderName)
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .read(false)
                .build();
    }
}