package com.bangkoo.back.controller.autoRecommend;

import com.bangkoo.back.service.autoRecommend.AutoRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AutoRecommendController {

    @Value("${ai.server.url}")
    private String aiServerUrl;  // AI 서버의 기본 URL

    private final AutoRecommendService autoRecommendService;

    /**
     * 최초 작성자: 김병훈
     * 최초 작성일: 2025-04-22
     *
     * 이미지 기반 가구 추천 API (AI 분석 후 추천)
     *
     * @param file MultipartFile 업로드된 이미지 파일
     * @return Redis에 저장된 추천 결과 확인 메시지
     */
    @PostMapping("/recommend/from_image")
    public ResponseEntity<?> recommendProductsFromImage(
            @RequestParam("file") MultipartFile file
    ) {

        // 1) 파일 유효성 검사
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 없습니다");
        }

        try {
            // 2) Redis 저장 키 생성 (실제 서비스에서는 사용자별 키로 변경)
            String redisKey = "user:recommend:temp";

            // 3) AI 서버 호출 및 Redis 저장
            //    - MultipartFile을 그대로 전달
            List<Map<String, Object>> result = autoRecommendService.analyzeAndSaveToRedis(file, redisKey);

            // 4) 저장 확인용 출력
            System.out.println("✅ Redis 저장 확인용 출력:");
            System.out.println("Key: " + redisKey);
            System.out.println("Value: " + result);

            return ResponseEntity.ok("추천 결과가 Redis에 저장되었습니다");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("이미지 처리 오류: " + e.getMessage());
        }
    }
}
