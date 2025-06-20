package org.ssafy.datacontest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.register.RegisterRequest;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.mapper.UserMapper;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void signUp(RegisterRequest registerRequest) {
        User user = UserMapper.toEntity(registerRequest, passwordEncoder);
        userRepository.save(user);
    }
}
