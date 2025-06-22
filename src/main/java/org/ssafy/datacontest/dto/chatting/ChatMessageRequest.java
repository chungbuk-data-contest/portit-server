package org.ssafy.datacontest.dto.chatting;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageRequest {
    private String roomId;
    private String senderEmail;
    private String senderName;    // UI용
    private String receiverEmail;
    private String content;
    private LocalDateTime sentAt;
}
