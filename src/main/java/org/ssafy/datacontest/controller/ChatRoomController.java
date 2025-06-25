package org.ssafy.datacontest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.chatroom.ChatRoomCreateRequest;
import org.ssafy.datacontest.dto.chatroom.ChatRoomCreateResponse;
import org.ssafy.datacontest.dto.chatroom.ChatRoomResponse;
import org.ssafy.datacontest.dto.chatting.ChatMessageResponse;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.entity.mongo.ChatMessage;
import org.ssafy.datacontest.service.ChatRoomService;

import java.util.List;

@RestController
@RequestMapping("/chat-rooms")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @PostMapping
    public ResponseEntity<ChatRoomCreateResponse> createChatRoom(@RequestBody ChatRoomCreateRequest chatRoomCreateRequest,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        String role = getRole(userDetails);

        ChatRoomCreateResponse chatRoomCreateResponse = chatRoomService.createChatRoom(chatRoomCreateRequest, userDetails.getUsername(), role);
        return ResponseEntity.ok(chatRoomCreateResponse);
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> readChatRoomsByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String role = getRole(userDetails);

        List<ChatRoomResponse> chatRoomResponses = chatRoomService.readChatRoomsByUser(userDetails.getUsername(), role);
        return ResponseEntity.ok(chatRoomResponses);
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<ChatMessageResponse> joinChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String role = getRole(userDetails);
        chatRoomService.joinChatRoom(roomId, userDetails.getUsername(), role);
        return ResponseEntity.ok().build();
    }

    private String getRole(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()                      // 여러 개 중 첫 번째
                .orElse("ROLE_USER");             // 없을 경우 기본값
    }
}
