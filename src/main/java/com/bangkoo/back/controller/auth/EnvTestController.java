package com.bangkoo.back.controller.auth; // ← 너의 실제 패키지명으로 바꿔줘

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnvTestController {

    @Value("${KAKAO_APP_CLIENT_ID:NOT_FOUND}")
    private String kakaoClientId;

    @GetMapping("/env/test")
    public ResponseEntity<String> testEnv() {
        return ResponseEntity.ok("KAKAO_APP_CLIENT_ID = " + kakaoClientId);
    }
}
