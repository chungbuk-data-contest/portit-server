package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ssafy.datacontest.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUserId(Long id);

    List<ChatRoom> findByCompanyCompanyId(Long id);

    ChatRoom findByUserIdAndCompanyCompanyIdAndArticleArtId(Long userId, Long companyId, Long articleId);
}
