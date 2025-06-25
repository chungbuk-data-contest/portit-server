package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.chatting.ChatMessageRequest;
import org.ssafy.datacontest.dto.chatting.ChatMessageResponse;
import org.ssafy.datacontest.entity.mongo.ChatMessage;

public interface ChatService {
    ChatMessageResponse saveMessage(Long roomId, ChatMessageRequest request, String loginId);

//    List<ChatMessageResponse> getChatRoomsByUserEmail(String email);
//
//    List<ChatMessage> getMessagesInRoom(Long roomId);
}
