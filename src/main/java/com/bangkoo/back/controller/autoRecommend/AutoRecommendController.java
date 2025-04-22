package com.bangkoo.back.controller.autoRecommend;

import com.bangkoo.back.model.product.Product;
import com.bangkoo.back.service.autoRecommend.AutoRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AutoRecommendController {

    private final AutoRecommendService autoRecommendService;

    /**
     * 이미지 + 카테고리 기반 가구 추천 API
     *
     * @param imageFile 사용자가 업로드한 방 이미지
     * @param category  원하는 가구 카테고리 (예: 침대, 소파 등)
     * @return 추천된 가구(Product) 리스트
     */
    @PostMapping("/recommend/from-image")
    public ResponseEntity<?> recommendProducts(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("category") String category
    ) {
        try {
            byte[] imageBytes = imageFile.getBytes();

            // AI 분석 + 카테고리 필터링 + 유사도 기반 추천
            List<Product> recommendedProducts = autoRecommendService.recommendProductsFromAI(imageBytes, category);

            return ResponseEntity.ok(recommendedProducts);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body("이미지 파일을 읽는 중 오류 발생");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("AI 서버와 통신 중 오류 발생");
        }
    }
}
