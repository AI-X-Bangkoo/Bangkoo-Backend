package com.bangkoo.back.controller.auth;

import com.bangkoo.back.model.DTO.TokenResponseDto;
import com.bangkoo.back.model.auth.User;
import com.bangkoo.back.service.auth.SocialOAuthService;
import com.bangkoo.back.service.auth.UserService;
import com.bangkoo.back.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SocialOAuthService socialOAuthService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 1. 프론트에서 카카오 로그인 URL 요청 시
    @GetMapping("/kakao/login")
    public ResponseEntity<?> kakaoLogin() {
        String clientId = "${KAKAO_APP_CLIENT_ID}";
        String redirectUri = "${KAKAO_REDIRECT_URI}";

        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize?" +
                "client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";

        return ResponseEntity.ok().body(Map.of("url", kakaoAuthUrl));
    }

    // 2. 카카오 인가 코드 받은 후, 백엔드 콜백 처리
    @GetMapping("/callback/kakao")
    public ResponseEntity<?> callback(@RequestParam("code") String code,
                                      HttpServletResponse response) {
        try {
            TokenResponseDto tokenDto = socialOAuthService.kakaoLogin(code);

            // JWT를 HttpOnly 쿠키로 설정
            jwtUtil.addJwtToCookie(response, tokenDto.getAccessToken());

            return ResponseEntity.ok(Map.of(
                    "message", "로그인 성공",
                    "nickname", tokenDto.getNickname()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/oauth/kakao")
    public ResponseEntity<TokenResponseDto> kakaoLogin(@RequestParam String code) throws Exception {
        System.out.println("🔥 카카오 로그인 컨트롤러 도착!");
        TokenResponseDto tokens = socialOAuthService.kakaoLogin(code);
        return ResponseEntity.ok(tokens);
    }
}
