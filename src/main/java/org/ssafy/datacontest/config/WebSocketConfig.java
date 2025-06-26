package org.ssafy.datacontest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.ssafy.datacontest.config.websocket.WebSocketSessionManager;
import org.ssafy.datacontest.jwt.JwtUtil;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final WebSocketSessionManager sessionManager;

    @Autowired
    public WebSocketConfig(JwtUtil jwtUtil,
                           WebSocketSessionManager sessionManager) {
        this.jwtUtil = jwtUtil;
        this.sessionManager = sessionManager;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // localhost:8080/ws/chat 으로 웹소켓 연결
        registry.addEndpoint("/ws/chat") // WebSocket 연결 엔드포인트
                .setAllowedOriginPatterns("*");
//                .withSockJS(); // SockJS fallback 지원
    }

    // 메시지를 라우팅하기 위한 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 구독할 주소(prefix)를 지정
        registry.enableSimpleBroker("/topic", "/queue"); // 메시지 구독 prefix
        // 클라이언트가 서버로 메시지를 보낼 때 사용할 prefix
        registry.setApplicationDestinationPrefixes("/app"); // 메시지 발행 prefix
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // 연결 시 JWT 토큰 검증 및 사용자 세션 등록
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        String loginId = jwtUtil.getLoginId(token);
                        accessor.setUser(new UsernamePasswordAuthenticationToken(loginId, null, List.of()));

                        // 세션 매니저에 사용자 등록 (실제 WebSocketSession은 여기서 직접 접근 불가)
                        // 대신 SUBSCRIBE 시점에서 처리
                    }
                } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    // 구독 시 채팅방 참여 처리
                    String destination = accessor.getDestination();
                    if (destination != null && destination.startsWith("/topic/")) {
                        String roomIdStr = destination.substring("/topic/".length());
                        try {
                            Long roomId = Long.parseLong(roomIdStr);
                            String loginId = accessor.getUser().getName();
                            sessionManager.addUserToRoom(roomId, loginId);
                        } catch (NumberFormatException e) {
                            // 잘못된 roomId 형식
                        }
                    }
                } else if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {
                    // 구독 해제 시 채팅방 나가기 처리
                    String destination = accessor.getDestination();
                    if (destination != null && destination.startsWith("/topic/")) {
                        String roomIdStr = destination.substring("/topic/".length());
                        try {
                            Long roomId = Long.parseLong(roomIdStr);
                            String loginId = accessor.getUser().getName();
                            sessionManager.removeUserFromRoom(roomId, loginId);
                        } catch (NumberFormatException e) {
                            // 잘못된 roomId 형식
                        }
                    }
                } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                    // 연결 해제 시 모든 세션 정보 제거
                    if (accessor.getUser() != null) {
                        String loginId = accessor.getUser().getName();
                        sessionManager.removeUserSession(loginId);
                    }
                }

                return message;
            }
        });
    }
}