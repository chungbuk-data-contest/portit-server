package org.ssafy.datacontest.dto.chatting;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageRequest {
    private Long roomId;
    private String content;
}
