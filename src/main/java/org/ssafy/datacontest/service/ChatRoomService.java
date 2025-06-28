package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.chatroom.ChatRoomCreateRequest;
import org.ssafy.datacontest.dto.chatroom.ChatRoomCreateResponse;
import org.ssafy.datacontest.dto.chatroom.ChatRoomJoinResponse;
import org.ssafy.datacontest.dto.chatroom.ChatRoomResponse;
import org.ssafy.datacontest.dto.chatting.ChatMessageResponse;

import java.util.List;

public interface ChatRoomService {
    ChatRoomCreateResponse createChatRoom(ChatRoomCreateRequest chatRoomCreateRequest, String loginId, String role);

    List<ChatRoomResponse> readChatRoomsByUser(String username, String role);

    ChatRoomJoinResponse joinAndGetRoomData(Long roomId, String username, String role);

    void leaveRoom(Long roomId, String username);
}
