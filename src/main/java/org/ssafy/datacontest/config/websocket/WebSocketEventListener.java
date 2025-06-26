package org.ssafy.datacontest.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
@Component
public class WebSocketEventListener {

    private final WebSocketSessionManager sessionManager;

    @Autowired
    public WebSocketEventListener(WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if (headerAccessor.getUser() != null) {
            String loginId = headerAccessor.getUser().getName();
            log.info("User {} connected with session {}", loginId, sessionId);
            // 실제 WebSocketSession은 여기서도 직접 접근하기 어려우므로
            // 연결된 사용자 정보만 저장
            sessionManager.addUserSession(loginId, null); // null로 일단 등록
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        if (headerAccessor.getUser() != null) {
            String loginId = headerAccessor.getUser().getName();
            log.info("User {} disconnected", loginId);
            sessionManager.removeUserSession(loginId);
        }
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();

        if (destination != null && destination.startsWith("/topic/") && headerAccessor.getUser() != null) {
            String roomIdStr = destination.substring("/topic/".length());
            try {
                Long roomId = Long.parseLong(roomIdStr);
                String loginId = headerAccessor.getUser().getName();
                sessionManager.addUserToRoom(roomId, loginId);
                log.info("User {} subscribed to room {}", loginId, roomId);
            } catch (NumberFormatException e) {
                log.warn("Invalid room ID format: {}", roomIdStr);
            }
        }
    }

    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();

        if (destination != null && destination.startsWith("/topic/") && headerAccessor.getUser() != null) {
            String roomIdStr = destination.substring("/topic/".length());
            try {
                Long roomId = Long.parseLong(roomIdStr);
                String loginId = headerAccessor.getUser().getName();
                sessionManager.removeUserFromRoom(roomId, loginId);
                log.info("User {} unsubscribed from room {}", loginId, roomId);
            } catch (NumberFormatException e) {
                log.warn("Invalid room ID format: {}", roomIdStr);
            }
        }
    }
}