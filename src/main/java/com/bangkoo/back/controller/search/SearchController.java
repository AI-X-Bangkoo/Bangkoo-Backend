package com.bangkoo.back.controller.search;

import com.bangkoo.back.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 최초 작성자: 김동규
 * 최초 작성일: 2025-04-03
 *
 * AI 추천 및 검색 컨트롤러
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 이미지 또는 텍스트 기반 AI 추천/검색 통합 요청
     *
     * @param image     이미지 파일 (선택)
     * @param query     텍스트 쿼리
     * @param minPrice  최소 가격
     * @param maxPrice  최대 가격
     * @param keyword   키워드
     * @param style     스타일
     * @return 추천 또는 검색 결과
     */
    @PostMapping(value = "/search", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> handleRecommendOrSearch(
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String image_url,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String style
    ) throws IOException {
        String result = searchService.recommendOrSearch(image, query, minPrice, maxPrice, keyword, style,image_url);
        return ResponseEntity.ok(result);
    }
}
