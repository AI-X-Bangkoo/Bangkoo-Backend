package com.bangkoo.back.controller.redis;

import com.bangkoo.back.service.redis.RedisService;
import com.bangkoo.back.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ RedisController
 * - 작성자: 김태원
 * - 작성일: 2025-04-12
 *
 * 🧠 Redis 기반 인테리어 상태 히스토리 관리 컨트롤러
 * - JWT를 통해 사용자 식별 후, 사용자별 히스토리 stack을 Redis에 저장
 * - 상태 저장(push), 되돌리기(undo), 다시 실행(redo), 현재 상태 조회 제공
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RedisController {

    private final RedisService redisService;
    private final JwtUtil jwtUtil;

    /**
     * 📌 상태 저장 (push)
     * - 사용자의 현재 상태를 undo 스택에 push
     * - redo 스택은 자동 clear됨
     */
    @PostMapping("/redis/state")
    public ResponseEntity<String> pushState(@RequestBody String base64, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(request));
        redisService.pushState(userId, base64);
        return ResponseEntity.ok("✅ 상태 저장 완료 (undo stack에 push)");
    }

    /**
     * 🔙 상태 되돌리기 (undo)
     * - undo 스택에서 pop → redo 스택으로 push
     * - 이전 상태 반환
     */
    @PostMapping("/redis/undo")
    public ResponseEntity<?> undo(HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(request));
        String result = redisService.undo(userId);
        return result == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    /**
     * 🔁 상태 다시 실행 (redo)
     * - redo 스택에서 pop → undo 스택으로 push
     * - 복원 상태 반환
     */
    @PostMapping("/redis/redo")
    public ResponseEntity<?> redo(HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(request));
        String result = redisService.redo(userId);
        return result == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    /**
     * 📂 현재 상태 조회
     * - 사용자별 current 상태 확인
     */
    @GetMapping("/redis/state")
    public ResponseEntity<?> getCurrent(HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(request));
        String current = redisService.getCurrentState(userId);
        return current == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(current);
    }
}
