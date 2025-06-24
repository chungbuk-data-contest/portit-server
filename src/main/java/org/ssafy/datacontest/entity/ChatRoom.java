package org.ssafy.datacontest.entity;

import jakarta.persistence.*;

@Entity
public class ChatRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomId; // 채팅방 ID (UUID 등으로 생성)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;
}
