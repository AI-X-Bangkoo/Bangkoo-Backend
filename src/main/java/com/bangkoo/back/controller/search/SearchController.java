package com.bangkoo.back.controller.search;

import com.bangkoo.back.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 최초 작성자: 김동규
 * 최초 작성일: 2025-04-03
 *
 * 검색 컨트롤러
 *
 * - 사용자의 쿼리를 기반으로 AI 서버에서 유사한 제품 검색
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 사용자의 쿼리를 기반으로 검색 실행
     *
     * @param query 사용자가 입력한 검색어
     * @param min_price 사용자가 선택한 최소 금액
     * @param max_price 사용자가 선택한 최대 금액
     * @param keyword 사용자가 선택한 키워드
     * @param style 사용자가 선택한 스타일
     * @return 검색 결과
     */
    @PostMapping
    public ResponseEntity<String> search(
            @RequestParam String query,
            @RequestParam(required = false) Integer min_price,
            @RequestParam(required = false) Integer max_price,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String style
    ) {
        String result = searchService.search(query, min_price, max_price, keyword, style);
        return ResponseEntity.ok(result);
    }

}
