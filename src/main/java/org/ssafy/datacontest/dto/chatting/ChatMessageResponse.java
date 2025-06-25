package org.ssafy.datacontest.dto.chatting;

import lombok.*;
import org.ssafy.datacontest.dto.chatroom.ChatRoomResponse;

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