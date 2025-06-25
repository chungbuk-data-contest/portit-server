package org.ssafy.datacontest.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.chatroom.ChatRoomCreateRequest;
import org.ssafy.datacontest.dto.chatroom.ChatRoomCreateResponse;
import org.ssafy.datacontest.dto.chatroom.ChatRoomJoinResponse;
import org.ssafy.datacontest.dto.chatroom.ChatRoomResponse;
import org.ssafy.datacontest.dto.chatting.ChatMessageResponse;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.ChatRoom;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.entity.mongo.ChatMessage;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.ChatRoomMapper;
import org.ssafy.datacontest.repository.*;
import org.ssafy.datacontest.service.ChatRoomService;

import java.util.Comparator;
import java.util.List;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ArticleRepository articleRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatRoomServiceImpl(UserRepository userRepository,
                               CompanyRepository companyRepository,
                               ArticleRepository articleRepository,
                               ChatRoomRepository chatRoomRepository,
                               ChatMessageRepository chatMessageRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.articleRepository = articleRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public ChatRoomCreateResponse createChatRoom(ChatRoomCreateRequest chatRoomCreateRequest, String loginId, String role) {
        Article article = articleRepository.findById(chatRoomCreateRequest.getArticleId())
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.ARTICLE_NOT_FOUND));
        User user = null;
        Company company = null;
        if(role.equals("ROLE_USER")){
            user = userRepository.findByLoginId(loginId);
            company = companyRepository.findById(chatRoomCreateRequest.getReceiverId())
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.COMPANY_NOT_FOUND));
        }
        else if(role.equals("ROLE_COMPANY")){
            company = companyRepository.findByLoginId(loginId);
            user = userRepository.findById(chatRoomCreateRequest.getReceiverId())
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND));
        }
        ChatRoom chatRoom = ChatRoomMapper.toEntity(user, company, article);
        chatRoomRepository.save(chatRoom);
        return ChatRoomMapper.toDto(chatRoom, article);
    }

    @Override
    public List<ChatRoomResponse> readChatRoomsByUser(String loginId, String role) {
        List<ChatRoom> rooms;

        if (role.equals("ROLE_USER")) {
            User user = userRepository.findByLoginId(loginId);
            rooms = chatRoomRepository.findByUserId(user.getId());
        } else {
            Company company = companyRepository.findByLoginId(loginId);
            rooms = chatRoomRepository.findByCompanyCompanyId(company.getCompanyId());
        }

        return rooms.stream().map(room -> {
                    // 최근 메시지 가져오기
                    ChatMessage lastMessage = (ChatMessage) chatMessageRepository.findTopByRoomIdOrderBySentAtDesc(room.getId()).orElse(null);

                    return ChatRoomResponse.builder()
                            .roomId(room.getId())
                            .partnerName(role.equals("ROLE_USER")
                                    ? room.getCompany().getCompanyName()
                                    : room.getUser().getNickname())
                            .thumbnailUrl(room.getArticle().getThumbnailUrl())
                            .lastMessage(lastMessage != null ? lastMessage.getContent() : "")
                            .sentAt(lastMessage != null ? lastMessage.getSentAt() : null)
                            .read(lastMessage == null || lastMessage.getSender().equals(loginId)) // 내가 보낸 메시지면 읽음으로 간주
                            .build();
                }).sorted(Comparator.comparing(ChatRoomResponse::getSentAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Transactional
    @Override
    public List<ChatMessageResponse> joinChatRoom(Long roomId, String username, String role) {
        // 해당 채팅방의 모든 메시지 중 내가 보낸 게 아닌 메시지 중 read = false 인 메시지를 찾아서 수정
        // 1. 읽지 않은 메시지 읽음 처리
        List<ChatMessage> unreadMessages = chatMessageRepository
                .findByRoomIdAndReadIsFalseAndSenderNot(roomId, username);

        unreadMessages.forEach(m -> m.setRead(true));
        chatMessageRepository.saveAll(unreadMessages);

        // 2. 전체 메시지 조회
        List<ChatMessage> allMessages = chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);

        // 3. DTO로 변환
        return allMessages.stream()
                .map(msg -> ChatMessageResponse.builder()
                        .sender(msg.getSender())
                        .content(msg.getContent())
                        .sentAt(msg.getSentAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public ChatRoomJoinResponse joinAndGetRoomData(Long roomId, String username, String role) {
        // 1. 메시지 읽음 처리 + 전체 메시지 조회
        List<ChatMessage> allMessages = chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);

        List<ChatMessage> unreadMessages = allMessages.stream()
                .filter(msg -> !msg.isRead() && !msg.getSender().equals(username))
                .toList();

        unreadMessages.forEach(m -> m.setRead(true));
        chatMessageRepository.saveAll(unreadMessages);

        List<ChatMessageResponse> messageResponses = allMessages.stream()
                .map(msg -> ChatMessageResponse.builder()
                        .sender(msg.getSender())
                        .content(msg.getContent())
                        .sentAt(msg.getSentAt())
                        .build())
                .toList();

        // 2. 채팅방 조회 및 예외처리
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.CHATROOM_NOT_FOUND));

        // 3. 작품 정보 추출
        Article article = chatRoom.getArticle();

        return ChatRoomJoinResponse.builder()
                .articleId(article.getArtId())
                .articleTitle(article.getTitle())
                .likeCount(article.getLikeCount())
                .thumbnailUrl(article.getThumbnailUrl())
                .messages(messageResponses)
                .build();
    }
}
