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
import org.ssafy.datacontest.dto.register.CompanyRegisterRequest;
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

    @Operation(summary = "유저 회원가입", description = "")
    @PostMapping("/register/user")
    public ResponseEntity<Void> signUp(@RequestBody RegisterRequest registerRequest){
        authService.userSignUp(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "유저 회원가입", description = "")
    @PostMapping("/register/company")
    public ResponseEntity<Void> signUp(@RequestBody CompanyRegisterRequest companyRegisterRequest){
        authService.companySignUp(companyRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
