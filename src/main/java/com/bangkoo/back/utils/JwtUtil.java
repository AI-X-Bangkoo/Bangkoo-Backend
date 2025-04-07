package com.bangkoo.back.utils;

import com.bangkoo.back.config.properites.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    private void init() {
        try {
            System.out.println("👉 JWT 키 초기화 시도");
            String key = jwtProperties.getSecretKey();
            System.out.println("🔑 가져온 키: " + key);

            if (key == null || key.isEmpty()) {
                System.out.println("❌ JWT 시크릿 키가 비어 있습니다.");
                throw new IllegalArgumentException("JWT 시크릿 키가 비어 있습니다.");
            }

            this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));
            System.out.println("✅ JWT 시크릿 키 초기화 완료");
        } catch (Exception e) {
            System.out.println("🔥 init 중 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // ========================== Token 생성 ==========================

    public String generateAccessToken(String email, String nickname) {
        System.out.println("▶️ Access Token 생성 - email: " + email + ", nickname: " + nickname);
        String token = Jwts.builder()
                .setSubject(email)
                .claim("nickname", nickname)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpirationMs()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        System.out.println("✅ Access Token 생성 완료: " + token);
        return token;
    }

    public String generateRefreshToken(String email) {
        System.out.println("▶️ Refresh Token 생성 - email: " + email);
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpirationMs()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        System.out.println("✅ Refresh Token 생성 완료: " + token);
        return token;
    }

    // ========================== 쿠키에 토큰 저장 ==========================

    public void addJwtToCookie(HttpServletResponse response, String token) {
        System.out.println("📦 Access Token을 쿠키에 저장 중...");
        Cookie cookie = new Cookie("Access_Token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtProperties.getAccessTokenExpirationMs() / 1000));
        cookie.setAttribute("SameSite", "None");

        response.addCookie(cookie);
        System.out.println("✅ Access Token 쿠키 저장 완료");
    }

    // ========================== 쿠키에서 토큰 추출 ==========================

    public String resolveToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    System.out.println("✅ Access Token 쿠키에서 추출 완료");
                    return cookie.getValue();
                }
            }
        }
        System.out.println("⚠️ Access Token 쿠키가 존재하지 않음");
        return null;
    }

    // ========================== 토큰 검증 및 인증 ==========================

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            boolean expired = isTokenExpired(token);
            System.out.println("✅ Token 유효성 검사 결과 - 만료 여부: " + expired);
            return !expired;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("❌ 토큰 유효성 검사 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        boolean result = getClaims(token).getExpiration().before(new Date());
        System.out.println("⌛ Token 만료 여부: " + result);
        return result;
    }

    public String getEmailFromToken(String token) {
        String email = getClaims(token).getSubject();
        System.out.println("📨 Token에서 추출한 이메일: " + email);
        return email;
    }

    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        System.out.println("🔐 인증 객체 생성 - email: " + email);
        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private Claims getClaims(String token) {
        System.out.println("🔍 토큰 클레임 파싱 중...");
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        System.out.println("📦 파싱된 Claims: " + claims.toString());
        return claims;
    }
}
