//package com.bangkoo.back.service.search;
//
//import com.bangkoo.back.utils.MultipartInputStreamFileResource;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
///**
// * 최초 작성자: 김동규
// * 최초 작성일: 2025-04-03
// *
// * 하이브리드 검색 요청 처리 서비스
// * - FastAPI 기반 AI 서버의 /search 엔드포인트와 통신
// */
//@Service
//public class SearchService {
//
//    private final RestTemplate restTemplate;
//
//    @Value("${ai.server.url}")
//    private String aiServerUrl;
//
//    public SearchService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    /**
//     * 사용자의 검색 쿼리를 FastAPI 기반 AI 서버에 전달하여
//     * 하이브리드 검색 결과(JSON)를 반환하는 메서드
//     *
//     * 1. HTTP 헤더 설정 (application/x-www-form-urlencoded)
//     * 2. 쿼리 파라미터를 본문에 추가
//     * 3. AI 서버의 /search 엔드포인트로 POST 요청 전송
//     * 4. 결과(JSON)를 문자열로 반환
//     *
//     * @param query 사용자 입력 쿼리
//     * @param min_price 사용자가 선택한 최소 금액
//     * @param max_price 사용자가 선택한 최대 금액
//     * @param keyword 사용자가 선택한 키워드
//     * @param style 사용자가 선택한 스타일
//     * @return AI 서버로부터 받은 검색 결과 JSON 문자열
//     */
//    public String search(String query, Integer minPrice, Integer maxPrice, String keyword, String style) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("query", query);
//        if (minPrice != null) body.add("min_price", String.valueOf(minPrice));
//        if (maxPrice != null) body.add("max_price", String.valueOf(maxPrice));
//        if (keyword != null) body.add("keyword", keyword);
//        if (style != null) body.add("style", style);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//        String url = aiServerUrl + "/api/search";
//        return restTemplate.postForObject(url, request, String.class);
//    }
//
//    public String searchByImage(MultipartFile file, String url, Integer minPrice, Integer maxPrice, String keyword, String style) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//
//            if (file != null) {
//                body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
//            } else if (url != null) {
//                body.add("url", url);
//            } else {
//                throw new IllegalArgumentException("file 또는 url 중 하나는 반드시 있어야 합니다.");
//            }
//
//            if (minPrice != null) body.add("min_price", minPrice.toString());
//            if (maxPrice != null) body.add("max_price", maxPrice.toString());
//            if (keyword != null) body.add("keyword", keyword);
//            if (style != null) body.add("style", style);
//
//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//            String fastapiUrl = aiServerUrl + "/search/image";
//            return new RestTemplate().postForObject(fastapiUrl, requestEntity, String.class);
//
//        } catch (IOException e) {
//            throw new RuntimeException("이미지 검색 실패", e);
//        }
//    }
//
//
//}
