package org.ssafy.datacontest.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.entity.Refresh;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.jwt.JwtUtil;
import org.ssafy.datacontest.repository.RefreshRepository;
import org.ssafy.datacontest.service.ReissueService;

import java.util.Date;

@Service
public class ReissueServiceImpl implements ReissueService {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Autowired
    public ReissueServiceImpl(JwtUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        // get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.REFRESH_TOKEN_NULL);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.REFRESH_TOKEN_INVALID);
        }

        boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String email = jwtUtil.getEmail(refresh);
        String role = jwtUtil.getRole(refresh);

        // make new JWT
        String newAccess = jwtUtil.generateToken("access", email, role, 600000L);
        String newRefresh = jwtUtil.generateToken("refresh", email, role, 86400000L);

        // Refresh Token 저장 DB에 기존의 Refresh Token 삭제 후 새 Refresh Token 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(email, newRefresh, 86400000L);

        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));
    }

    private void addRefreshEntity(String email, String newRefresh, long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refresh = new Refresh();
        refresh.setRefresh(newRefresh);
        refresh.setEmail(email);
        refresh.setExpiration(date.toString());

        refreshRepository.save(refresh);
        // TODO: Redis 사용해 만료시간 설정
//        TTL 설정을 통해 자동으로 Refresh 토큰이 삭제되면 무방하지만 계속해서 토큰이 쌓일 경우 용량 문제가 발생할 수 있다.
//        따라서 스케줄 작업을 통해 만료시간이 지난 토큰은 주기적으로 삭제하는 것이 올바르다.
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
//        cookie.setSecure(true);
//        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
