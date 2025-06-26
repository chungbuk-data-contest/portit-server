package org.ssafy.datacontest.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.config.websocket.WebSocketSessionManager;
import org.ssafy.datacontest.dto.chatting.ChatMessageRequest;
import org.ssafy.datacontest.dto.chatting.ChatMessageResponse;
import org.ssafy.datacontest.entity.ChatRoom;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.entity.mongo.ChatMessage;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.repository.ChatMessageRepository;
import org.ssafy.datacontest.repository.ChatRoomRepository;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.ChatService;
import org.ssafy.datacontest.util.FcmUtil;

import java.time.LocalDateTime;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final WebSocketSessionManager webSocketSessionManager;
    private final FcmUtil fcmUtil;

    @Autowired
    public ChatServiceImpl(ChatMessageRepository chatMessageRepository,
                           ChatRoomRepository chatRoomRepository,
                           UserRepository userRepository,
                           CompanyRepository companyRepository,
                           WebSocketSessionManager webSocketSessionManager,
                           FcmUtil fcmUtil) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.webSocketSessionManager = webSocketSessionManager;
        this.fcmUtil = fcmUtil;
    }

    @Override
    @Transactional
    public ChatMessageResponse saveMessage(Long roomId, ChatMessageRequest request, String loginId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.CHATROOM_NOT_FOUND));

        // MongoDB에 저장할 sender: loginId
        ChatMessage message = ChatMessage.builder()
                .roomId(roomId)
                .sender(loginId)
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .read(false)
                .build();

        chatMessageRepository.save(message);

        // 상대방 정보 및 FCM 토큰 확인
        String receiverLoginId = getReceiverLoginId(room, loginId);
        String senderName = getSenderName(room, loginId);

        // 상대방이 해당 채팅방에 접속 중인지 확인
        if (!webSocketSessionManager.isUserInRoom(roomId, receiverLoginId)) {
            // 상대방이 접속 중이 아니면 FCM 푸시 알림 전송
            sendPushNotification(receiverLoginId, senderName, request.getContent(), room);
        }

        return ChatMessageResponse.builder()
                .sender(senderName)
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .read(false)
                .build();
    }

    /**
     * 상대방의 loginId를 반환
     */
    private String getReceiverLoginId(ChatRoom room, String senderLoginId) {
        if (room.getUser().getLoginId().equals(senderLoginId)) {
            return room.getCompany().getLoginId();
        } else if (room.getCompany().getLoginId().equals(senderLoginId)) {
            return room.getUser().getLoginId();
        } else {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_USER);
        }
    }

    /**
     * 발신자의 표시 이름을 반환 (닉네임 또는 회사명)
     */
    private String getSenderName(ChatRoom room, String senderLoginId) {
        if (room.getUser().getLoginId().equals(senderLoginId)) {
            return room.getUser().getNickname();
        } else if (room.getCompany().getLoginId().equals(senderLoginId)) {
            return room.getCompany().getCompanyName();
        } else {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_USER);
        }
    }

    private void sendPushNotification(String receiverLoginId, String senderName, String messageContent, ChatRoom room) {
        String fcmToken = getFcmToken(receiverLoginId);

        if (fcmToken != null && !fcmToken.isEmpty()) {
            String title = senderName + "님이 메시지를 보냈습니다";

            // 추가 데이터로 채팅방 정보 전송
            boolean success = fcmUtil.sendNotificationWithDataMessage(
                    fcmToken,
                    title,
                    messageContent,
                    room.getId().toString()
            );

            if (success) {
                System.out.println("FCM 푸시 알림 전송 성공 - 수신자: " + receiverLoginId);
            } else {
                System.out.println("FCM 푸시 알림 전송 실패 - 수신자: " + receiverLoginId);
            }
        } else {
            System.out.println("FCM 토큰이 없습니다 - 수신자: " + receiverLoginId);
        }
    }

    private String getFcmToken(String loginId) {
        User user = userRepository.findByLoginId(loginId);
        if (user != null) {
            return user.getFcmToken();
        }
        Company company = companyRepository.findByLoginId(loginId);
        if (company != null) {
            return company.getFcmToken();
        }

        return null;
    }
}