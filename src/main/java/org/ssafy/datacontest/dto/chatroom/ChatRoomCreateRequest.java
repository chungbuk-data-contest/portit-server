package org.ssafy.datacontest.dto.chatroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomCreateRequest {
    private Long articleId;
    private Long receiverId;
}
