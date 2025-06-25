package org.ssafy.datacontest.mapper;

import org.ssafy.datacontest.dto.chatroom.ChatRoomCreateResponse;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.ChatRoom;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.User;

public class ChatRoomMapper {
    public static ChatRoom toEntity(User user, Company company, Article article) {
        return ChatRoom.builder()
                .user(user)
                .company(company)
                .article(article)
                .build();
    }

    public static ChatRoomCreateResponse toDto(ChatRoom chatRoom, Article article) {
        return ChatRoomCreateResponse.builder()
                .roomId(chatRoom.getId())
                .articleTitle(article.getTitle())
                .likeCount(article.getLikeCount())
                .thumbnailUrl(article.getThumbnailUrl())
                .build();
    }
}
