//package com.bangkoo.back.controller.search_none;
//
//import com.bangkoo.back.service.search_none.SearchLogService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
///**
// * 최초 작성자: 김동규
// * 최초 작성일: 2025-04-03
// *
// * 검색 로그 컨트롤러
// *
// * - 사용자의 검색어를 MongoDB에 저장하고,
// *   최근 검색어 / 인기 검색어를 조회하는 API
// */
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class SearchLogController {
//
//    private final SearchLogService searchLogService;
//
//    /**
//     * 사용자의 검색어를 MongoDB에 기록
//     *
//     * @param query 검색어
//     * @param userId 사용자 ID
//     * @return 상태 코드 200 (ok)
//     */
//    @PostMapping("/log-search")
//    public ResponseEntity<Void> logSearch(@RequestParam String query, @RequestParam String userId) {
//        searchLogService.logSearch(query, userId);
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * 해당 사용자의 최근 검색어 목록 조회
//     *
//     * @param userId 사용자 ID
//     * @return 최근 검색어 리스트
//     */
//    @GetMapping("/recent-searches")
//    public ResponseEntity<?> getRecentSearches(@RequestParam String userId) {
//        return ResponseEntity.ok(searchLogService.getRecentSearches(userId));
//    }
//
//    /**
//     * 전체 사용자 기준으로 인기 검색어 TOP 10 조회
//     *
//     * @return 인기 검색어 리스트
//     */
//    @GetMapping("/popular-searches")
//    public ResponseEntity<?> getPopularSearches() {
//        return ResponseEntity.ok(searchLogService.getPopularSearches());
//    }
//}
