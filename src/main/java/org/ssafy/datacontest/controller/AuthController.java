package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.dto.register.RegisterRequest;
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

    @Operation(summary = "회원가입", description = "")
    @PostMapping("/register")
    public ResponseEntity<Void> signUp(@RequestBody RegisterRequest registerRequest){
        authService.signUp(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @Operation(summary = "로그인", description = "HTTP 반환값 200일 시 로그인 성공")
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
//        LoginResponse login = authService.login(loginRequest);
//        return ResponseEntity.ok(login);
//    }

}
