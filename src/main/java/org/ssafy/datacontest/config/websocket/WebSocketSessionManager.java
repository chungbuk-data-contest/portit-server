package org.ssafy.datacontest.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Slf4j
@Component
public class WebSocketSessionManager {

    // loginId -> WebSocketSession 매핑
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    // roomId -> Set<loginId> 매핑 (특정 채팅방에 접속한 사용자들)
    private final Map<Long, Set<String>> roomUsers = new ConcurrentHashMap<>();

    /**
     * 사용자 세션 등록
     */
    public void addUserSession(String loginId, WebSocketSession session) {
        userSessions.put(loginId, session);
        log.info("User {} connected to WebSocket", loginId);
    }

    /**
     * 사용자 세션 제거
     */
    public void removeUserSession(String loginId) {
        userSessions.remove(loginId);
        // 모든 채팅방에서 해당 사용자 제거
        roomUsers.values().forEach(users -> users.remove(loginId));
        log.info("User {} disconnected from WebSocket", loginId);
    }

    /**
     * 특정 채팅방에 사용자 추가
     */
    public void addUserToRoom(Long roomId, String loginId) {
        roomUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(loginId);
        log.info("User {} joined room {}", loginId, roomId);
    }

    /**
     * 특정 채팅방에서 사용자 제거
     */
    public void removeUserFromRoom(Long roomId, String loginId) {
        Set<String> users = roomUsers.get(roomId);
        if (users != null) {
            users.remove(loginId);
            if (users.isEmpty()) {
                roomUsers.remove(roomId);
            }
        }
        log.info("User {} left room {}", loginId, roomId);
    }

    /**
     * 사용자가 WebSocket에 접속중인지 확인
     */
    public boolean isUserOnline(String loginId) {
        WebSocketSession session = userSessions.get(loginId);
        return session != null && session.isOpen();
    }

    /**
     * 사용자가 특정 채팅방에 접속중인지 확인
     */
    public boolean isUserInRoom(Long roomId, String loginId) {
        Set<String> users = roomUsers.get(roomId);
        return users != null && users.contains(loginId);
    }

    /**
     * 특정 채팅방의 접속자 수 반환
     */
    public int getRoomUserCount(Long roomId) {
        Set<String> users = roomUsers.get(roomId);
        return users != null ? users.size() : 0;
    }

    /**
     * 사용자의 WebSocket 세션 반환
     */
    public WebSocketSession getUserSession(String loginId) {
        return userSessions.get(loginId);
    }
}