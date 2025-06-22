package org.ssafy.datacontest.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.entity.redis.Refresh;
import org.ssafy.datacontest.jwt.JwtUtil;
import org.ssafy.datacontest.repository.RefreshRepository;
import org.ssafy.datacontest.service.ReissueService;
import org.ssafy.datacontest.validation.RefreshValidation;

@Service
public class ReissueServiceImpl implements ReissueService {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final RefreshValidation refreshValidation;

    @Autowired
    public ReissueServiceImpl(JwtUtil jwtUtil,
                              RefreshRepository refreshRepository,
                              RefreshValidation refreshValidation) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.refreshValidation = refreshValidation;
    }

    @Override
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        // get refresh token
        String refresh = extractRefreshTokenFromCookies(request);
        refreshValidation.validateRefreshToken(refresh);

        String loginId = jwtUtil.getLoginId(refresh);
        String role = jwtUtil.getRole(refresh);

        // make new JWT
        String newAccess = jwtUtil.generateToken("access", loginId, role, 600000L);
        String newRefresh = jwtUtil.generateToken("refresh", loginId, role, 86400000L);

        // Refresh Token 저장 DB에 기존의 Refresh Token 삭제 후 새 Refresh Token 저장
        refreshRepository.deleteById(loginId);
        addRefreshEntity(loginId, newRefresh, 86400000L);

        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));
    }

    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals("refresh")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void addRefreshEntity(String loginId, String newRefresh, long expiredMs) {
        Refresh refresh = new Refresh(loginId, newRefresh, expiredMs);
        refreshRepository.save(refresh);
//        TTL 설정을 통해 자동으로 Refresh 토큰이 삭제되면 무방하지만 계속해서 토큰이 쌓일 경우 용량 문제가 발생할 수 있다.
//        따라서 스케줄 작업을 통해 만료시간이 지난 토큰은 주기적으로 삭제하는 것이 올바르다.
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
//        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
