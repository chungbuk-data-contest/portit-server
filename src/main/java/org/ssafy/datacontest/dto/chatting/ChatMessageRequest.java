package org.ssafy.datacontest.dto.chatting;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageRequest {
    private Long roomId;
    private String sender;
    private String content;
    private LocalDateTime sentAt;
}
