package org.ssafy.datacontest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

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
}