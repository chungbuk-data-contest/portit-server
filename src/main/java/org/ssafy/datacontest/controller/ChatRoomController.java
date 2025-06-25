package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.chatroom.ChatRoomCreateRequest;
import org.ssafy.datacontest.dto.chatroom.ChatRoomCreateResponse;
import org.ssafy.datacontest.dto.chatroom.ChatRoomJoinResponse;
import org.ssafy.datacontest.dto.chatroom.ChatRoomResponse;
import org.ssafy.datacontest.dto.chatting.ChatMessageResponse;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.entity.mongo.ChatMessage;
import org.ssafy.datacontest.service.ChatRoomService;

import java.util.List;

@Tag(name = "ChatRoom")
@RestController
@RequestMapping("/chat-rooms")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @Operation(summary = "채팅방 생성", description = "사용자 또는 기업이 채팅방 생성.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<ChatRoomCreateResponse> createChatRoom(@RequestBody ChatRoomCreateRequest chatRoomCreateRequest,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        String role = getRole(userDetails);

        ChatRoomCreateResponse chatRoomCreateResponse = chatRoomService.createChatRoom(chatRoomCreateRequest, userDetails.getUsername(), role);
        return ResponseEntity.ok(chatRoomCreateResponse);
    }


    @Operation(summary = "사용자의 채팅방 목록 조회", description = "로그인한 사용자 또는 기업이 참여 중인 채팅방 목록 조회.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> readChatRoomsByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String role = getRole(userDetails);

        List<ChatRoomResponse> chatRoomResponses = chatRoomService.readChatRoomsByUser(userDetails.getUsername(), role);
        return ResponseEntity.ok(chatRoomResponses);
    }

    @Operation(summary = "채팅방 입장", description = "사용자 또는 기업이 특정 채팅방에 입장.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 입장 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방이 존재하지 않음")
    })
    @PostMapping("/{roomId}/join")
    public ResponseEntity<ChatRoomJoinResponse> joinChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String role = getRole(userDetails);

        ChatRoomJoinResponse response = chatRoomService.joinAndGetRoomData(roomId, userDetails.getUsername(), role);

        return ResponseEntity.ok(response);
    }

    private String getRole(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()                      // 여러 개 중 첫 번째
                .orElse("ROLE_USER");             // 없을 경우 기본값
    }
}
