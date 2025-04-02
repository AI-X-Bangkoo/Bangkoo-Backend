package com.bangkoo.back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * 최초 작성자 : 김동규
 * 최초 작성일 : 2025-04-01
 *
 * Spring Security 보안 설정 클래스
 * */
@Configuration
public class SecurityConfig {

    /**
     * 보안 필터 체인 설정
     * - CORS 설정 적용
     * - CSRF 비활성화
     * - 세션 상태를 STATELESS로 설정 (JWT 기반 인증을 위함)
     * - 모든 요청을 허용 (개발용 설정, 추후 인증 필요)
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 보안 필터 체인
     * @throws Exception 예외 발생 시
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, InMemoryClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorize"))
                        .redirectionEndpoint(redirection -> redirection.baseUri("/login/oauth2/code/kakao"))
                        .clientRegistrationRepository(clientRegistrationRepository())
                );

        // JWT 필터 추가 예정
        // http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 설정 Bean
     * - 프론트엔드 요청을 허용하기 위해 도메인, 메서드, 헤더 등 설정
     * - allowCredentials(true): 인증정보 포함 허용 (예: 쿠키)
     *
     * @return CorsConfigurationSource CORS 설정 정보
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 프론트엔드 URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1시간 동안 preflight 요청 캐싱

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 인증 매니저 Bean
     * - AuthenticationManager를 수동으로 등록
     *
     * @param configuration AuthenticationConfiguration 객체
     * @return AuthenticationManager 인증 매니저
     * @throws Exception 예외 발생 시
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 비밀번호 암호화용 PasswordEncoder Bean
     * - BCrypt 알고리즘 사용
     *
     * @return PasswordEncoder 인코더 객체
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JWT 인증 필터 Bean 등록 예정
    // @Bean
    // public JwtAuthenticationFilter jwtAuthenticationFilter() {
    //     return new JwtAuthenticationFilter();
    // }

    /**
     * 카카오 클라이언트 등록 Bean
     * - 카카오 OAuth2 로그인 정보 등록
     *
     * @return ClientRegistrationRepository 카카오 클라이언트 등록 저장소
     */
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.kakakoClientRegistration());
    }

    /**
     * 카카오 OAuth2 클라이언트 등록 정보
     *
     * @return ClientRegistration 카카오 클라이언트 등록 객체a
     */
    private ClientRegistration kakakoClientRegistration() {
        return ClientRegistration.withRegistrationId("kakao")
                .clientId("${KAKAO_APP_CLIENT_ID}")
                .clientSecret("${KAKAO_APP_CLIENT_SECRET}")
                .scope("profile_nickname","account_email")
                .authorizationUri("https://kakao.com/oauth2/authorize")
                .tokenUri("https://kakao.com/oauth2/token")
                .userInfoUri("https://kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .clientName("kakao")
                .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
                .build();
    }
}
