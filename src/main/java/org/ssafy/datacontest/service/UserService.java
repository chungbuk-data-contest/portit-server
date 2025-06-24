package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.user.UserResponse;
import org.ssafy.datacontest.dto.user.UserUpdateRequest;

public interface UserService {
    UserResponse getUser(String username, Long userId);
    Long updateUser(UserUpdateRequest userUpdateRequest, String userName, Long userId);
}
