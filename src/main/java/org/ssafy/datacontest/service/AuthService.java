package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.register.RegisterRequest;

public interface AuthService {
    void signUp(RegisterRequest registerRequest);
}
