package com.bangkoo.back.service.auth;

import com.bangkoo.back.config.properites.JwtProperties;
import com.bangkoo.back.config.properites.SocialOAuthProperties;
import com.bangkoo.back.model.DTO.TokenResponseDTO;
import com.bangkoo.back.model.auth.User;
import com.bangkoo.back.repository.auth.UserRepository;
import com.bangkoo.back.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SocialOAuthService {

    /**
     * 카카오 OAuth인증을 통해 사용자의 정보를 가져오고,
     * JWT토큰을 발급하여 클라이언트에게 응답하는 기능
     */

    private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final SecretKey secretKey;
    private final int accessTokenExpiration;
    private final long refreshTokenExpiration = 1000L * 60 * 60 * 24 * 7; // 7일
    private final UserRepository userRepository;

    public SocialOAuthService(
            SocialOAuthProperties oAuthProps,
            @Qualifier("jwtProperties") JwtProperties jwtProps,
            JwtUtil jwtUtil,
            UserRepository userRepository
    ) {
        this.jwtProperties = jwtProps;
        this.jwtUtil = jwtUtil;
        this.restTemplate = new RestTemplate();
        this.clientId = oAuthProps.getClientId();
        this.clientSecret = oAuthProps.getClientSecret();
        this.redirectUri = oAuthProps.getRedirectUri();
        this.userRepository = userRepository;

        if (jwtProps.getSecretKey() == null || jwtProps.getSecretKey().isEmpty()) {
            throw new IllegalArgumentException("JWT secret key must not be empty");
        }
        this.secretKey = jwtUtil.getSecretKey(jwtProps.getSecretKey());

        if (jwtProps.getAccessTokenExpirationMs() == null) {
            throw new IllegalArgumentException("Access token expiration time must be set");
        }
        this.accessTokenExpiration = jwtProps.getAccessTokenExpirationMs().intValue();
    }

    // 메서드 시그니처 수정
    public TokenResponseDTO kakaoLogin(String code) throws Exception {
        log.info("🎯 Received Kakao authorization code: {}", code);

        String kakaoAccessToken = getAccessToken(code);
        Map<String, Object> userInfo = getKakaoUserInfo(kakaoAccessToken);

        String email = getKakaoEmail(userInfo);
        String nickname = getKakaoNickname(userInfo);

        if (email == null || email.isEmpty()) {
            throw new Exception("Cannot retrieve email from Kakao account");
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder().email(email).nickname(nickname).build();
            return userRepository.save(newUser);
        });

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getNickname());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        log.info("✅ Generated accessToken: {}", accessToken);

        return TokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .nickname(user.getNickname())
                .login(true)
                .build();
    }



    private String getAccessToken(String code) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        if (clientSecret != null && !clientSecret.isEmpty()) {
            params.add("client_secret", clientSecret);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URI, request, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null || !body.containsKey("access_token")) {
            throw new Exception("Failed to get Kakao access token: " + body);
        }

        return (String) body.get("access_token");
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(USER_INFO_URI, HttpMethod.GET, request, Map.class);

        Map<String, Object> userInfo = response.getBody();
        if (userInfo == null) {
            throw new Exception("Failed to fetch Kakao user info");
        }

        return userInfo;
    }

    private String getKakaoEmail(Map<String, Object> userInfo) {
        return Optional.ofNullable(userInfo)
                .map(u -> (Map<String, Object>) u.get("kakao_account"))
                .map(account -> (String) account.get("email"))
                .orElse(null);
    }

    private String getKakaoNickname(Map<String, Object> userInfo) {
        return Optional.ofNullable(userInfo)
                .map(u -> (Map<String, Object>) u.get("kakao_account"))
                .map(account -> (Map<String, Object>) account.get("profile"))
                .map(profile -> (String) profile.get("nickname"))
                .orElse(null);
    }

    private ResponseCookie createCookie(String name, String value, long maxAgeMs) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAgeMs / 1000)
                .sameSite("Lax")
                .build();
    }
}
