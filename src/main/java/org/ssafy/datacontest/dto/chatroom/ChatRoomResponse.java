package org.ssafy.datacontest.dto.chatroom;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomResponse {
    private Long roomId;
    private String partnerName;
    private String thumbnailUrl;
    private String lastMessage;
    private boolean read;
    private LocalDateTime sentAt;
}