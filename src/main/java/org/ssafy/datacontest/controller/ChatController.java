package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.ssafy.datacontest.dto.chatting.ChatMessageRequest;
import org.ssafy.datacontest.dto.chatting.ChatRoomResponse;
import org.ssafy.datacontest.entity.ChatMessage;
import org.ssafy.datacontest.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
@Tag(name = "Chatting", description = "웹소켓 기반 채팅 API, 개발 중")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/send")
    public void sendMessage(ChatMessageRequest request) {
        // 1. MongoDB에 저장
        chatService.saveMessage(request);

        // 2. 수신자에게 실시간 전송
        messagingTemplate.convertAndSend(
                "/queue/" + request.getReceiverEmail(),  // 수신자 전용 채널
                request
        );
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getUserChatRooms(@RequestParam String email) {
        return ResponseEntity.ok(chatService.getChatRoomsByUserEmail(email));
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessage>> getRoomMessages(@RequestParam String roomId) {
        return ResponseEntity.ok(chatService.getMessagesInRoom(roomId));
    }
}
