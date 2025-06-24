package org.ssafy.datacontest.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.ssafy.datacontest.entity.mongo.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    // 특정 사용자가 보낸 또는 받은 채팅방 목록 조회
    List<ChatMessage> findBySenderEmailOrReceiverEmail(String senderEmail, String receiverEmail);

    // 특정 채팅방의 메시지 내역 조회 (시간 순)
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(String roomId);

    List<ChatMessage> findDistinctRoomIdBySenderEmailOrReceiverEmail(String email, String email1);
}
