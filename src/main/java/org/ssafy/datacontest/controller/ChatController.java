package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.chatting.ChatMessageRequest;
import org.ssafy.datacontest.dto.chatting.ChatMessageResponse;
import org.ssafy.datacontest.entity.mongo.ChatMessage;
import org.ssafy.datacontest.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "http://127.0.0.1:5500")
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

    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId,
                            ChatMessageRequest request,
                            Principal principal) {
        ChatMessage savedMessage = chatService.saveMessage(roomId, request, principal.getName());
        messagingTemplate.convertAndSend("/topic/" + roomId, savedMessage);
    }

//    @GetMapping("/rooms")
//    public ResponseEntity<List<ChatMessageResponse>> getUserChatRooms(@RequestParam String email) {
//        return ResponseEntity.ok(chatService.getChatRoomsByUserEmail(email));
//    }
//
//    @GetMapping("/messages")
//    public ResponseEntity<List<ChatMessage>> getRoomMessages(@RequestParam Long roomId) {
//        return ResponseEntity.ok(chatService.getMessagesInRoom(roomId));
//    }
}
