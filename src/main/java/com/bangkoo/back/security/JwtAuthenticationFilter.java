package com.bangkoo.back.security;

import com.bangkoo.back.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request); // 쿠키와 헤더에서 토큰을 추출

        if (token != null && jwtUtil.isValidToken(token)) {
            String email = jwtUtil.getEmailFromToken(token); // 토큰에서 이메일 추출
            String role = jwtUtil.getUserRoleFromToken(token); // 토큰에서 역할 추출 (예: ADMIN, USER)

            System.out.println("사용자 역할: " + role);
            if (email != null && !email.isEmpty()) {
                // 사용자 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, jwtUtil.getAuthentication(token).getAuthorities());

                // 인증 객체에 WebAuthenticationDetailsSource 설정 (IP, 세션 정보 등)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 객체 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 인증 정보 로그로 확인
                System.out.println("Authentication successfully set in SecurityContext: " + authentication);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰");
                return;
            }
        } else if (token != null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 유효하지 않음");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // 요청에서 토큰 추출하는 메서드 (쿠키 + 헤더 모두 허용)
    private String extractToken(HttpServletRequest request) {
        // 1. 쿠키에서 토큰 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 2. Authorization 헤더에서 토큰 추출 (Bearer 방식)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // "Bearer " 이후의 토큰 값
        }

        return null; // 토큰이 없으면 null 반환
    }
}
