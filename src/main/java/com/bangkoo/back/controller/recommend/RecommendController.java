package com.bangkoo.back.controller.recommend;

import com.bangkoo.back.service.recommend.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 최초 작성자: 김동규
 * 최초 작성일: 2025-04-03
 *
 * AI 추천 요청 컨트롤러
 *
 * - 클라이언트로부터 이미지와 쿼리를 받아 AI 서버에 전달하고, 결과를 반환
 */
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    /**
     * 사용자로부터 방 이미지와 쿼리를 받아 AI 추천 결과 반환
     *
     * @param image 이미지 파일
     * @param query 추천 요청 텍스트
     * @param min_price 최소 금액
     * @param max_price 최대 금액
     * @param keyword 키워드 텍스트
     * @param style 스타일 텍스트
     * @return AI 추천 결과
     */
    @PostMapping
    public ResponseEntity<String> recommend(
            @RequestPart MultipartFile image,
            @RequestPart String query,
            @RequestPart(required = false) Integer min_price,
            @RequestPart(required = false) Integer max_price,
            @RequestPart(required = false) String keyword,
            @RequestPart(required = false) String style
    ) throws IOException {
        String result = recommendService.recommend(image, query, min_price, max_price, keyword, style);
        return ResponseEntity.ok(result);
    }

}
