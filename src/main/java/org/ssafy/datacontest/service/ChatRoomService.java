package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.chatroom.ChatRoomRequest;
import org.ssafy.datacontest.dto.chatroom.ChatRoomResponse;
public interface ChatRoomService {
    ChatRoomResponse createChatRoom(ChatRoomRequest chatRoomRequest, String loginId, String role);
}
