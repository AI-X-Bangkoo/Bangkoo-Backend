//package com.bangkoo.back.controller.recommend;
//
//import com.bangkoo.back.service.recommend.RecommendService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
///**
// * 최초 작성자: 김동규
// * 최초 작성일: 2025-04-03
// *
// * AI 추천 요청 컨트롤러
// */
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class RecommendController_20250407_backup {
//
//    private final RecommendService recommendService;
//
//    /**
//     * 이미지 기반 AI 추천 요청
//     *
//     * @param image     이미지 파일
//     * @param query     추천 텍스트
//     * @param min_price 최소 가격
//     * @param max_price 최대 가격
//     * @param keyword   키워드
//     * @param style     스타일
//     * @return 추천 결과
//     */
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> recommend(
//            @RequestPart("image") MultipartFile image,
//            @RequestParam("query") String query,
//            @RequestParam(value = "min_price", required = false) Integer minPrice,
//            @RequestParam(value = "max_price", required = false) Integer maxPrice,
//            @RequestParam(value = "keyword", required = false) String keyword,
//            @RequestParam(value = "style", required = false) String style
//    ) throws IOException {
//        String result = recommendService.recommend(image, query, minPrice, maxPrice, keyword, style);
//        return ResponseEntity.ok(result);
//    }
//
//    @PostMapping("/recommend-or-search")
//    public ResponseEntity<String> handleRecommendOrSearch(
//            @RequestParam MultipartFile image,
//            @RequestParam String query,
//            @RequestParam(required = false) Integer minPrice,
//            @RequestParam(required = false) Integer maxPrice,
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) String style
//    ) throws IOException {
//        String result = recommendService.recommendOrSearch(image, query, minPrice, maxPrice, keyword, style);
//        return ResponseEntity.ok(result);
//    }
//
//}
