package org.ssafy.datacontest.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_message")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    private String id;

    private String roomId;
    private String senderEmail;
    private String receiverEmail;
    private String content;
    private LocalDateTime sentAt;
    private boolean read; // 기본 false로 저장, 수신자가 채팅방 입장하면 true로 변경
}