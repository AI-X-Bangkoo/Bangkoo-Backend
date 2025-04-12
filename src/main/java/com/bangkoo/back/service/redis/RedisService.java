package com.bangkoo.back.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * ✅ 새로운 상태 push
     * - undo 스택에 push
     * - redo 스택은 clear
     */
    public void pushState(String userId, String base64) {
        String undoKey = getUndoKey(userId);
        String redoKey = getRedoKey(userId);

        redisTemplate.opsForList().leftPush(undoKey, base64);
        redisTemplate.delete(redoKey); // 새로 저장했으니 redo 초기화
    }

    /**
     * 🔙 undo 수행
     * - undo에서 pop → redo로 push
     * - 그다음 undo 스택 가장 위에 있는 게 current
     */
    public String undo(String userId) {
        String undoKey = getUndoKey(userId);
        String redoKey = getRedoKey(userId);

        // 최소 하나 이상 있어야 함 (현재 상태를 pop할 거니까)
        Long size = redisTemplate.opsForList().size(undoKey);
        if (size == null || size <= 1) return null;

        // 현재 상태 pop → redo에 push
        String popped = redisTemplate.opsForList().leftPop(undoKey);
        if (popped != null) {
            redisTemplate.opsForList().leftPush(redoKey, popped);
        }

        return redisTemplate.opsForList().index(undoKey, 0); // 현재 상태
    }

    /**
     * 🔁 redo 수행
     * - redo에서 pop → undo에 push
     */
    public String redo(String userId) {
        String undoKey = getUndoKey(userId);
        String redoKey = getRedoKey(userId);

        String popped = redisTemplate.opsForList().leftPop(redoKey);
        if (popped != null) {
            redisTemplate.opsForList().leftPush(undoKey, popped);
        }

        return redisTemplate.opsForList().index(undoKey, 0); // 현재 상태
    }

    /**
     * 📂 현재 상태 조회
     */
    public String getCurrentState(String userId) {
        String undoKey = getUndoKey(userId);
        return redisTemplate.opsForList().index(undoKey, 0);
    }

    /**
     * 🧹 전체 초기화
     */
    public void clearAll(String userId) {
        redisTemplate.delete(getUndoKey(userId));
        redisTemplate.delete(getRedoKey(userId));
    }

    // 👉 키 조합 도우미
    private String getUndoKey(String userId) {
        return "undo:" + userId;
    }

    private String getRedoKey(String userId) {
        return "redo:" + userId;
    }
}
