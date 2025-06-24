package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.user.UserResponse;

public interface UserService {
    UserResponse getUser(String username, Long userId);
}
