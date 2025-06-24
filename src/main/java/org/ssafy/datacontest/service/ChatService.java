package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.chatting.ChatMessageRequest;
import org.ssafy.datacontest.dto.chatting.ChatMessageResponse;
import org.ssafy.datacontest.entity.mongo.ChatMessage;

import java.util.List;

public interface ChatService {
    ChatMessage saveMessage(Long roomId, ChatMessageRequest request);

    List<ChatMessageResponse> getChatRoomsByUserEmail(String email);

    List<ChatMessage> getMessagesInRoom(Long roomId);
}
