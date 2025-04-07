package com.bangkoo.back.service.auth;

import com.bangkoo.back.config.properites.JwtProperties;
import com.bangkoo.back.config.properites.SocialOAuthProperties;
import com.bangkoo.back.model.DTO.TokenResponseDto;
import com.bangkoo.back.model.auth.User;
import com.bangkoo.back.repository.auth.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class SocialOAuthService {

    private final JwtProperties jwtProperties;
    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final SecretKey secretKey;
    private final Integer expirationMs;
    private final long refreshExpirationMs = 1000L * 60 * 60 * 24 * 7; // 7일
    private final UserRepository userRepository;

    private final String tokenUri = "https://kauth.kakao.com/oauth/token";
    private final String userInfoUri = "https://kapi.kakao.com/v2/user/me";

    public SocialOAuthService(SocialOAuthProperties oAuthProperties,
                              JwtProperties jwtProperties,
                              UserRepository userRepository) {
        this.jwtProperties = jwtProperties;
        this.restTemplate = new RestTemplate();
        this.clientId = oAuthProperties.getClientId();
        this.clientSecret = oAuthProperties.getClientSecret();
        this.redirectUri = oAuthProperties.getRedirectUri();
        this.userRepository = userRepository;

        if (jwtProperties.getSecretKey() == null || jwtProperties.getSecretKey().isEmpty()) {
            throw new IllegalArgumentException("The secret key for JWT cannot be null or empty.");
        }
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));

        if (jwtProperties.getAccessTokenExpirationMs() == null) {
            throw new IllegalArgumentException("Access token expiration time cannot be null.");
        }
        this.expirationMs = Math.toIntExact(jwtProperties.getAccessTokenExpirationMs());
    }

    public TokenResponseDto kakaoLogin(String code) throws Exception {
        log.info("🎯 카카오 인가 코드 수신: {}", code);

        String accessToken = getAccessToken(code);
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);

        String email = getKakaoEmail(userInfo);
        String nickname = getKakaoNickname(userInfo);

        if (email == null || email.isEmpty()) {
            throw new Exception("이메일 정보를 가져올 수 없습니다. 카카오 계정 설정을 확인하세요.");
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .build();
            return userRepository.save(newUser);
        });

        return generateJwtToken(user.getEmail(), user.getNickname());
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

        log.info("🔐 카카오 토큰 요청 파라미터:\n{}", params);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        log.info("📥 카카오 토큰 응답 상태: {}", response.getStatusCode());
        log.info("📥 카카오 토큰 응답 바디: {}", response.getBody());

        Map<String, Object> body = response.getBody();
        if (body == null || !body.containsKey("access_token")) {
            throw new Exception("카카오 토큰 요청 실패: " + String.valueOf(body));
        }

        return (String) body.get("access_token");
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, Map.class);
        return response.getBody();
    }

    private String getKakaoEmail(Map<String, Object> userInfo) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        return kakaoAccount.get("email").toString();
    }

    private String getKakaoNickname(Map<String, Object> userInfo) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return profile.get("nickname").toString();
    }

    private TokenResponseDto generateJwtToken(String email, String nickname) {
        long now = System.currentTimeMillis();

        String accessToken = Jwts.builder()
                .setSubject(email)
                .claim("nickname", nickname)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshExpirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(email)
                .nickname(nickname)
                .build();
    }
}
