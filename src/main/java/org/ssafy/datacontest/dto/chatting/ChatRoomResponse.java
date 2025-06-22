package org.ssafy.datacontest.dto.chatting;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatRoomResponse {
    private String roomId;
    private String lastMessage;
    private LocalDateTime sentAt;
    private String partnerName; // 필요 시
    private boolean read; // 상대가 안 읽은 메시지 여부
}
