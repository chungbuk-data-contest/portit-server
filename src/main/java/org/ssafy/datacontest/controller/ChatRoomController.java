package org.ssafy.datacontest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.dto.chatroom.ChatRoomRequest;
import org.ssafy.datacontest.dto.chatroom.ChatRoomResponse;
import org.ssafy.datacontest.dto.chatting.ChatMessageResponse;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.service.ChatRoomService;

@RestController
@RequestMapping("/chat-rooms")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody ChatRoomRequest chatRoomRequest,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        // ROLE_USER, ROLE_COMPANY 중 하나만 있다고 가정할 때
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // "ROLE_USER" 등
                .findFirst()                      // 여러 개 중 첫 번째
                .orElse("ROLE_USER");             // 없을 경우 기본값

        ChatRoomResponse chatRoomResponse = chatRoomService.createChatRoom(chatRoomRequest, userDetails.getUsername(), role);
        return ResponseEntity.ok(chatRoomResponse);
    }
}
