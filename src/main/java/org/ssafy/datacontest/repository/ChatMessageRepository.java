package org.ssafy.datacontest.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.ssafy.datacontest.entity.mongo.ChatMessage;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    // 특정 채팅방의 메시지 내역 조회 (시간 순)
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(Long roomId);

    List<ChatMessage> findBySender(String email);

    Optional<Object> findTopByRoomIdOrderBySentAtDesc(Long id);

    List<ChatMessage> findByRoomIdAndReadIsFalseAndSenderNot(Long roomId, String username);
}
