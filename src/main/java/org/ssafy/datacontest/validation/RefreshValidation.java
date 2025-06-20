package org.ssafy.datacontest.validation;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.jwt.JwtUtil;
import org.ssafy.datacontest.repository.RefreshRepository;

@Component
public class RefreshValidation {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Autowired
    public RefreshValidation(JwtUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    public void validateRefreshToken(String refresh) {
        checkNull(refresh);
        checkExpired(refresh);
        checkCategory(refresh);
        checkExistence(refresh);
    }

    private void checkNull(String refresh) {
        if (refresh == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.REFRESH_TOKEN_NULL);
        }
    }

    private void checkExpired(String refresh) {
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
    }

    private void checkCategory(String refresh) {
        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    private void checkExistence(String refresh) {
        boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
    }
}