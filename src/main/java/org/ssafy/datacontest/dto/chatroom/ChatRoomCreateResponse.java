package org.ssafy.datacontest.dto.chatroom;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomCreateResponse {
    private Long roomId;
    private String articleTitle;
    private Long likeCount;
    private String thumbnailUrl;
}
