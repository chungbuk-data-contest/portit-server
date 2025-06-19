package org.ssafy.datacontest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.register.RegisterRequest;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.repository.UserRepository;

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
        User user = RegisterRequest.toEntity(registerRequest, passwordEncoder);
        userRepository.save(user);
    }
}
