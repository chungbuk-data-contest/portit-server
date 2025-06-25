package org.ssafy.datacontest.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.dto.register.LoginRequest;
import org.ssafy.datacontest.entity.redis.Refresh;
import org.ssafy.datacontest.repository.RefreshRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    // Spring Security는 일련의 필터들에서 사용자 요청 검증
    // LoginFilter는 UsernamePasswordAuthenticationFilter의 정의에 따라 /login 경로로 오는 POST 요청 검증
    // 필터단에서 login 요청을 검증, 응답하기 때문에 컨트롤러에서 처리할 필요가 없음.
    private static final long ACCESS_TOKEN_VALIDITY = 1_800_000L; // 30분
    private static final long REFRESH_TOKEN_VALIDITY = 86_400_000L; // 1일
    private final RefreshRepository refreshRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager,
                       RefreshRepository refreshRepository,
                       JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.refreshRepository = refreshRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        LoginRequest loginRequest;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginRequest = objectMapper.readValue(messageBody, LoginRequest.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String loginId = loginRequest.getLoginId();
        String password = loginRequest.getPassword();

//        String username = obtainUsername(request);
//        String password = obtainPassword(request);

        // 스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, password, null);

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        String loginId = userDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        //토큰 생성
        String access = jwtUtil.generateToken("access", loginId, role, ACCESS_TOKEN_VALIDITY);
        String refresh = jwtUtil.generateToken("refresh", loginId, role, REFRESH_TOKEN_VALIDITY);

        // Refresh Token 저장
//        addRefreshEntity(loginId, refresh, 86400000L);
        Refresh redisRefresh = new Refresh(loginId, refresh, REFRESH_TOKEN_VALIDITY);
        refreshRepository.deleteById(loginId);
        refreshRepository.save(redisRefresh);
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"role\":\"" + role + "\"}");
        logger.info("successful authentication");
    }

//    private void addRefreshEntity(String email, String refresh, Long expiredMs) {
//        Date date = new Date(System.currentTimeMillis() + expiredMs);
//
//        Refresh refreshEntity = Refresh.builder()
//                .email(email)
//                .refresh(refresh)
//                .expiration(date.toString())
//                .build();
//
//        refreshRepository.save(refreshEntity);
//    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        logger.info("unsuccessful authentication");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}