package org.ssafy.datacontest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.ssafy.datacontest.jwt.JwtFilter;
import org.ssafy.datacontest.jwt.JwtUtil;
import org.ssafy.datacontest.jwt.LoginFilter;
import org.ssafy.datacontest.repository.RefreshRepository;

@Configuration
@EnableWebSecurity // Security를 위한 Config기 때문에
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguration 객체 생성자 주입
    private final RefreshRepository refreshRepository;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;

    public SecurityConfig(RefreshRepository refreshRepository,
                          AuthenticationConfiguration authenticationConfiguration,
                          JwtUtil jwtUtil) {
        this.refreshRepository = refreshRepository;
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        LoginFilter loginFilter = new LoginFilter(refreshRepository, authenticationManager(authenticationConfiguration), jwtUtil);
        loginFilter.setFilterProcessesUrl("/auth/login");
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        // 세션 방식에서는 세션이 고정되기 때문에 csrf 방식을 방어를 해줘야 한다.
        // 하지만 jwt 방식은 세션을 스테이트리스상태로 관리해 방어하지 않아도 된다.
        http
                .csrf(AbstractHttpConfigurer::disable);

        // form 로그인 방식 disable
        http
                .formLogin(AbstractHttpConfigurer::disable);

        // http basic 인증 방식 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/" ,"auth/register", "/reissue").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());

        // jwt는 session이 필요없으니
        // session을 stateless 상태로. 이게 제일 중요
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        http
//                .addFilterBefore(new LogoutFilter(jwtUtil, refreshRepository), org.springframework.security.web.authentication.logout.LogoutFilter.class);

        http
                .addFilterAfter(new JwtFilter(jwtUtil), LoginFilter.class);
        //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요

        http
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        // CORS code
//        http
//                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
//                    @Override
//                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//                        CorsConfiguration config = new CorsConfiguration();
//
//                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
//                        config.setAllowedMethods(Collections.singletonList("*"));
//                        config.setAllowCredentials(true);
//                        config.setAllowedHeaders(Collections.singletonList("*"));
//                        config.setMaxAge(3600L);
//                        return config;
//                    }}));
        // CORS code
        return http.build();
    }
}
