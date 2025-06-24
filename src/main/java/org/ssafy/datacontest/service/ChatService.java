package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.chatting.ChatMessageRequest;
import org.ssafy.datacontest.dto.chatting.ChatRoomResponse;
import org.ssafy.datacontest.entity.mongo.ChatMessage;

import java.util.List;

public interface ChatService {
    void saveMessage(ChatMessageRequest request);

    List<ChatRoomResponse> getChatRoomsByUserEmail(String email);

    List<ChatMessage> getMessagesInRoom(Long roomId);
}
