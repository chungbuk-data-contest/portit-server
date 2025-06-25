package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.company.CompanySummaryResponse;
import org.ssafy.datacontest.dto.user.UserAlertResponse;
import org.ssafy.datacontest.dto.user.UserResponse;
import org.ssafy.datacontest.dto.user.UserUpdateRequest;

import java.util.List;

public interface UserService {
    UserResponse getUser(String username);
    Long updateUser(UserUpdateRequest userUpdateRequest, String userName);
    List<UserAlertResponse> getUserAlerts(String userName);
    CompanySummaryResponse checkAlert(Long alertId, String userName);
}
