package org.ssafy.datacontest.dto.chatroom;

import lombok.*;
import org.ssafy.datacontest.dto.chatting.ChatMessageResponse;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomJoinResponse {
    private Long articleId;
    private String articleTitle;
    private Long likeCount;
    private String thumbnailUrl;

    private List<ChatMessageResponse> messages;
}