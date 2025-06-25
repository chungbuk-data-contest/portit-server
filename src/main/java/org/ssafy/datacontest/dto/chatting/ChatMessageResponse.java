package org.ssafy.datacontest.dto.chatting;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String sender;
    private String content;
    private LocalDateTime sentAt;
}