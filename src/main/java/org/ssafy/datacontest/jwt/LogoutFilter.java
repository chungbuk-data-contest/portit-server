package org.ssafy.datacontest.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.repository.RefreshRepository;
import org.ssafy.datacontest.repository.UserRepository;

import java.io.IOException;

public class LogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public LogoutFilter(JwtUtil jwtUtil,
                        RefreshRepository refreshRepository,
                        UserRepository userRepository,
                        CompanyRepository companyRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        // path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^/auth/logout$")) {
            chain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if(!requestMethod.equals("POST")) {
            chain.doFilter(request, response);
            return;
        }

        // get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("refresh")){
                refresh = cookie.getValue();
            }
        }

        // refresh null check
        if(refresh == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // expired check
        try{
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh 인지 확인(발급 시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if(!category.equals("refresh")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // DB에 저장되어 있는 지 확인
        String loginId = jwtUtil.getLoginId(refresh);
        boolean isExist = refreshRepository.existsById(loginId);
        if(!isExist){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String role = jwtUtil.getRole(refresh);
        // 로그아웃 진행
        // Refresh Token을 DB에서 제거
        refreshRepository.deleteById(loginId);
        // fcm token null 처리
        deleteFcmToken(loginId, role);
        // Refresh Token Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void deleteFcmToken(String loginId, String role) {
        if ("ROLE_USER".equals(role)) {
            User user = userRepository.findByLoginId(loginId);
            if (user != null) {
                user.setFcmToken(null);
                userRepository.save(user);
            }
        } else if ("ROLE_COMPANY".equals(role)) {
            Company company = companyRepository.findByLoginId(loginId);
            if (company != null) {
                company.setFcmToken(null);
                companyRepository.save(company);
            }
        }
    }
}