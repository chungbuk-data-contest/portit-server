package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.dto.user.UserAlertResponse;
import org.ssafy.datacontest.dto.user.UserResponse;
import org.ssafy.datacontest.dto.user.UserUpdateRequest;
import org.ssafy.datacontest.service.UserService;
import retrofit2.http.Path;

import java.util.List;

@RestController
@Tag(name="User", description = "유저 관련 API")
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/mypage")
    @Operation(
            summary = "유저 조회",
            description = "유저 정보를 조회합니다."
    )
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getUser(userDetails.getUsername()));
    }

    @PutMapping("")
    @Operation(
            summary = "유저 정보 수정",
            description = "수정되지 않은 필드도 다 보내주세요."
    )
    public ResponseEntity<Long> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.updateUser(userUpdateRequest, userDetails.getUsername()));
    }

    @GetMapping("/alert")
    @Operation(
            summary = "유저 알림 조회",
            description = "유저의 모든 알림 조회"
    )
    public ResponseEntity<List<UserAlertResponse>> getUserAlert(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserAlerts(userDetails.getUsername()));
    }
}
