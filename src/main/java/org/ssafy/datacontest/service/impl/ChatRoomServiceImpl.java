package org.ssafy.datacontest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.chatroom.ChatRoomRequest;
import org.ssafy.datacontest.dto.chatroom.ChatRoomResponse;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.ChatRoom;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.ChatRoomMapper;
import org.ssafy.datacontest.repository.ArticleRepository;
import org.ssafy.datacontest.repository.ChatRoomRepository;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.ChatRoomService;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ArticleRepository articleRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatRoomServiceImpl(UserRepository userRepository,
                               CompanyRepository companyRepository,
                               ArticleRepository articleRepository,
                               ChatRoomRepository chatRoomRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.articleRepository = articleRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public ChatRoomResponse createChatRoom(ChatRoomRequest chatRoomRequest, String loginId, String role) {
        Article article = articleRepository.findById(chatRoomRequest.getArticleId())
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.ARTICLE_NOT_FOUND));
        User user = null;
        Company company = null;
        if(role.equals("ROLE_USER")){
            user = userRepository.findByLoginId(loginId);
            company = companyRepository.findById(chatRoomRequest.getReceiverId())
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.COMPANY_NOT_FOUND));
        }
        else if(role.equals("ROLE_COMPANY")){
            company = companyRepository.findByLoginId(loginId);
            user = userRepository.findById(chatRoomRequest.getReceiverId())
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND));
        }
        ChatRoom chatRoom = ChatRoomMapper.toEntity(user, company, article);
        chatRoomRepository.save(chatRoom);
        return ChatRoomMapper.toDto(chatRoom, article);
    }
}
