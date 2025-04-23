package com.bangkoo.back.controller.autoRecommend;

import com.bangkoo.back.dto.product.ProductsResponseDTO;
import com.bangkoo.back.model.product.Product;
import com.bangkoo.back.service.autoRecommend.AutoRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AutoRecommendController {

    private final AutoRecommendService autoRecommendService;

    /**
     * base64 인코딩된 이미지 기반 가구 추천 API (AI 분석 후 추천)
     *
     * @param file 방 이미지
     * @return 추천된 가구(Product) 리스트
     */
    @PostMapping("/recommend/from-image")
    public ResponseEntity<?> recommendProductsFromImage(
            @RequestParam("file") MultipartFile file
    ) {
        System.out.println("********Received image: " + file);
        try {
            // 이미지를 AI 서버로 보내기
            Map<String, Object> aiAnalysisResult = autoRecommendService.sendImageToAI(file);

            if (aiAnalysisResult != null && !aiAnalysisResult.isEmpty()) {
                // 분석 결과 기반 가구 추천
                List<ProductsResponseDTO> recommendedProducts = autoRecommendService.recommendProductsFromAIAnalysis(aiAnalysisResult);
                return ResponseEntity.ok(recommendedProducts);
            } else {
                return ResponseEntity.internalServerError().body("AI 서버로부터 분석 결과를 받지 못했습니다.");
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("이미지 처리 오류: " + e.getMessage());
        }
    }
}
