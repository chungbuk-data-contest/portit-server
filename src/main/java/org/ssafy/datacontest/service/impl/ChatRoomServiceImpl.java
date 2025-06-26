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
    public ChatRoomCreateResponse createChatRoom(ChatRoomCreateRequest req, String loginId, String role) {
        Article article = articleRepository.findById(req.getArticleId())
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.ARTICLE_NOT_FOUND));

        User user;
        Company company;
        if (role.equals("ROLE_USER")) {
            user = findUser(loginId);
            company = findCompany(req.getReceiverId());
        } else {
            company = findCompany(loginId);
            user = findUser(req.getReceiverId());
        }

        ChatRoom room = chatRoomRepository.findByUserIdAndCompanyCompanyIdAndArticleArtId(user.getId(), company.getCompanyId(), article.getArtId());
        if (room != null) {
            return ChatRoomMapper.toDto(room, article, getPartnerName(room, role));
        }

        ChatRoom newRoom = ChatRoomMapper.toEntity(user, company, article);
        chatRoomRepository.save(newRoom);
        return ChatRoomMapper.toDto(newRoom, article, getPartnerName(newRoom, role));
    }


    @Override
    public List<ChatRoomResponse> readChatRoomsByUser(String loginId, String role) {
        List<ChatRoom> rooms = role.equals("ROLE_USER")
                ? chatRoomRepository.findByUserId(findUser(loginId).getId())
                : chatRoomRepository.findByCompanyCompanyId(findCompany(loginId).getCompanyId());

        return rooms.stream()
                .map(room -> {
                    ChatMessage last = getLastMessage(room.getId());

                    boolean isRead;
                    if (last == null) {
                        isRead = true;
                    } else if (last.getSender().equals(loginId)) {
                        isRead = true;
                    } else {
                        isRead = last.isRead();
                    }

                    return ChatRoomResponse.builder()
                            .roomId(room.getId())
                            .partnerName(getPartnerName(room, role))
                            .thumbnailUrl(room.getArticle().getThumbnailUrl())
                            .lastMessage(last != null ? last.getContent() : "")
                            .sentAt(last != null ? last.getSentAt() : null)
                            .read(isRead)
                            .build();
                })
                .sorted(Comparator.comparing(ChatRoomResponse::getSentAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Transactional
    @Override
    public ChatRoomJoinResponse joinAndGetRoomData(Long roomId, String username, String role) {
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
        messages.stream()
                .filter(m -> !m.isRead() && !m.getSender().equals(username))
                .forEach(m -> m.setRead(true));
        chatMessageRepository.saveAll(messages);

        List<ChatMessageResponse> responseList = messages.stream()
                .map(m -> ChatMessageResponse.builder()
                        .sender(m.getSender())
                        .content(m.getContent())
                        .sentAt(m.getSentAt())
                        .read(m.isRead())
                        .build())
                .toList();

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.CHATROOM_NOT_FOUND));

        Article article = room.getArticle();

        return ChatRoomJoinResponse.builder()
                .articleId(article.getArtId())
                .articleTitle(article.getTitle())
                .likeCount(article.getLikeCount())
                .thumbnailUrl(article.getThumbnailUrl())
                .partnerName(getPartnerName(room, role))
                .messages(responseList)
                .build();
    }

    private User findUser(String loginId) {
        User user = userRepository.findByLoginId(loginId);
        if (user == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    private Company findCompany(String loginId) {
        Company company = companyRepository.findByLoginId(loginId);
        if (company == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.COMPANY_NOT_FOUND);
        }
        return company;
    }

    private String getPartnerName(ChatRoom room, String role) {
        return role.equals("ROLE_USER")
                ? room.getCompany().getCompanyName()
                : room.getUser().getNickname();
    }

    private ChatMessage getLastMessage(Long roomId) {
        return chatMessageRepository.findTopByRoomIdOrderBySentAtDesc(roomId).orElse(null);
    }
}
