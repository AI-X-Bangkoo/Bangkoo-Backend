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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;
    private static final String COOKIE_NAME = "accessToken";

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        String key = jwtProperties.getSecretKey();
        System.out.println("JWT Raw Key: " + key);
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("JWT secret key is missing.");
        }
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));
    }

    // ====== JWT 생성 ======
    public String generateAccessToken(String email, String nickname) {
        return Jwts.builder()
                .setSubject(email)
                .claim("nickname", nickname)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpirationMs()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpirationMs()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ====== 쿠키 저장 ======
    public void addJwtToCookie(HttpServletResponse response, String token) {
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String cookie = COOKIE_NAME + "=" + encodedToken + ";"
                + "HttpOnly; Secure; SameSite=None; Path=/; Max-Age=" + (jwtProperties.getAccessTokenExpirationMs() / 1000);
        response.addHeader("Set-Cookie", cookie);
    }

    public void addNicknameToCookie(HttpServletResponse response, String nickname) {
        String encoded = URLEncoder.encode(nickname, StandardCharsets.UTF_8);
        String cookie = "nickname=" + encoded + "; Path=/; Max-Age=" + (jwtProperties.getAccessTokenExpirationMs() / 1000)
                + "; Secure; SameSite=None";
        response.addHeader("Set-Cookie", cookie);
    }

    // ✅ 로그아웃 시 쿠키 제거
    public void removeCookies(HttpServletResponse response) {
        String expiredAccessToken = COOKIE_NAME + "=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=None";
        String expiredNickname = "nickname=; Path=/; Max-Age=0; Secure; SameSite=None";

        response.addHeader("Set-Cookie", expiredAccessToken);
        response.addHeader("Set-Cookie", expiredNickname);
    }

    // ====== 토큰 파싱 ======
    public String resolveToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public boolean isValidToken(String token) {
        try {
            getClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        return new UsernamePasswordAuthenticationToken(email, "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
    // ====== 기타 유틸 ======
    public SecretKey getSecretKey(String key) {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));
    }


}
