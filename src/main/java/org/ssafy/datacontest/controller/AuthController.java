package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.register.CompanyRegisterRequest;
import org.ssafy.datacontest.dto.register.LoginRequest;
import org.ssafy.datacontest.dto.register.UserRegisterRequest;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.service.AuthService;

@Tag(name = "Auth", description = "")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "유저 회원가입", description = "")
    @PostMapping("/register/user")
    public ResponseEntity<Void> userSignUp(@RequestBody UserRegisterRequest userRegisterRequest){
        authService.userSignUp(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "기업 회원가입", description = "")
    @PostMapping("/register/company")
    public ResponseEntity<Void> companySignUp(@RequestBody CompanyRegisterRequest companyRegisterRequest){
        authService.companySignUp(companyRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "유저 loginId 중복 검사", description = "User, Company 모두 존재하지 않는 Id만 가능")
    @GetMapping("/check/user/loginId")
    public ResponseEntity<Void> checkUserLoginId(@RequestParam String loginId) {
        authService.checkUserLoginId(loginId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 nickname 중복 검사", description = "User 전용")
    @GetMapping("/check/user/nickname")
    public ResponseEntity<Void> checkUserNickname(@RequestParam String nickname) {
        authService.checkUserNickname(nickname);
        return ResponseEntity.ok().build();
    }

}
