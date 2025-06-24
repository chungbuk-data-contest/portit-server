package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.dto.user.UserResponse;
import org.ssafy.datacontest.dto.user.UserUpdateRequest;
import org.ssafy.datacontest.service.UserService;
import retrofit2.http.Path;

@RestController
@Tag(name="User", description = "유저 관련 API")
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("userId") Long userId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getUser(userDetails.getUsername(), userId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Long> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, @PathVariable("userId") Long userId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.updateUser(userUpdateRequest, userDetails.getUsername(), userId));
    }
}
