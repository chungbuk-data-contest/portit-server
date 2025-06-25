package org.ssafy.datacontest.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.register.CompanyRegisterRequest;
import org.ssafy.datacontest.dto.register.UserRegisterRequest;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.CompanyMapper;
import org.ssafy.datacontest.mapper.UserMapper;
import org.ssafy.datacontest.repository.ArticleRepository;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.AuthService;
import org.ssafy.datacontest.service.CompanyService;
import org.ssafy.datacontest.service.UserService;
import org.ssafy.datacontest.validation.CompanyValidation;
import org.ssafy.datacontest.validation.UserValidation;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidation userValidation;
    private final CompanyValidation companyValidation;
    private final ArticleRepository articleRepository;

    @Autowired
    public AuthServiceImpl(CompanyRepository companyRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserValidation userValidation,
                           CompanyValidation companyValidation, ArticleRepository articleRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidation = userValidation;
        this.companyValidation = companyValidation;
        this.articleRepository = articleRepository;
    }

    @Override
    public void userSignUp(UserRegisterRequest userRegisterRequest) {
        userValidation.validateUser(userRegisterRequest);
        User user = UserMapper.toEntity(userRegisterRequest, passwordEncoder);
        userRepository.save(user);
    }

    @Override
    public void companySignUp(CompanyRegisterRequest companyRegisterRequest) {
        companyValidation.validateCompany(companyRegisterRequest);
        Company company = CompanyMapper.toEntity(companyRegisterRequest, passwordEncoder);
        companyRepository.save(company);
    }

    @Override
    public void checkUserLoginId(String loginId) {
        boolean existsUser = userRepository.existsByLoginId(loginId);
        boolean existsCompany = companyRepository.existsByLoginId(loginId);
        if(existsUser || existsCompany) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATED_ID);
        }
    }

    @Override
    public void checkUserNickname(String nickname) {
        boolean existsUser = userRepository.existsByNickname(nickname);
        if(existsUser) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATED_NICKNAME);
        }
    }

    @Override
    @Transactional
    public void deleteAccount(String username) {
        User user = userRepository.findByLoginId(username);
        Company company = companyRepository.findByLoginId(username);

        if(user == null && company == null)
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.USER_NOT_FOUND);

        if(user != null) deleteUser(user);
        if(company != null) deleteCompany(company);
    }

    private void deleteUser(User user){
        user.setDeleted(true);
        // 작품 비공개 처리 필요
        List<Article> articleList = articleRepository.findByUser_Id(user.getId());
        for(Article article : articleList) {
            article.setDeleted(true);
        }
    }

    private void deleteCompany(Company company){
        company.setDeleted(true);
    }
}
