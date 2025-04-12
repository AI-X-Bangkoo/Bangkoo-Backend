package com.bangkoo.back.controller.redis;

import com.bangkoo.back.service.redis.RedisService;
import com.bangkoo.back.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RedisController
 * - 인테리어 상태 관리 (undo / redo)
 * - 작성자: 김태원
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RedisController {

    private final RedisService redisService;
    private final JwtUtil jwtUtil;

    /**
     * 📌 현재 상태 push (새로운 상태 저장)
     */
    @PostMapping("/redis/state")
    public ResponseEntity<String> pushState(@RequestBody String base64, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(request));
        redisService.pushState(userId, base64);
        return ResponseEntity.ok("✅ 상태 저장 완료 (undo stack에 push)");
    }

    /**
     * 🔙 되돌리기 (undo)
     */
    @PostMapping("/redis/undo")
    public ResponseEntity<?> undo(HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(request));
        String result = redisService.undo(userId);
        return result == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    /**
     * 🔁 다시 실행 (redo)
     */
    @PostMapping("/redis/redo")
    public ResponseEntity<?> redo(HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(request));
        String result = redisService.redo(userId);
        return result == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    /**
     * 📂 현재 상태 조회
     */
    @GetMapping("/redis/state")
    public ResponseEntity<?> getCurrent(HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(request));
        String current = redisService.getCurrentState(userId);
        return current == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(current);
    }
}
