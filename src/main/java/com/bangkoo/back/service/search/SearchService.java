package com.bangkoo.back.service.search;

import com.bangkoo.back.utils.MultipartInputStreamFileResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 최초 작성자: 김동규
 * 최초 작성일: 2025-04-03
 *
 * AI 추천 요청 서비스 (Gemini + 유사도 검색 통합)
 */
@Service
public class SearchService {

    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public SearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 이미지와 쿼리를 받아 /search 통합 API 호출
     * 이미지가 없으면 텍스트 기반 검색, 쿼리에 따라 추천/검색 자동 분기
     */
    public String recommendOrSearch(
            MultipartFile image,
            String query,
            Integer minPrice,
            Integer maxPrice,
            String keyword,
            String style,
            String image_url
    ) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("query", query);

        if (image != null && !image.isEmpty()) {
            body.add("image", new MultipartInputStreamFileResource(image.getInputStream(), image.getOriginalFilename()));
        }

        if (image_url != null && !image_url.isEmpty()) {
            body.add("image_url", image_url);
        }

        if (minPrice != null) body.add("min_price", minPrice);
        if (maxPrice != null) body.add("max_price", maxPrice);
        if (image_url != null) body.add("image_url", image_url);
        if (keyword != null) body.add("keyword", keyword);
        if (style != null) body.add("style", style);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        String fastapiUrl = aiServerUrl + "/search";
        return restTemplate.postForObject(fastapiUrl, request, String.class);
    }

    /**
     * 최근 검색어 목록을 AI 서버로부터 조회
     *
     * @return 최근 검색어 리스트 (최대 10개)
     */
    public List<String> getRecentSearches() {
        String url = aiServerUrl + "/api/recent-searches";
        return restTemplate.getForObject(url, List.class);
    }

    /**
     * 인기 검색어 목록을 AI 서버로부터 조회
     *
     * @return 검색 횟수 기준으로 정렬된 인기 검색어 리스트
     */
    public List<Map<String, Object>> getPopularSearches() {
        String url = aiServerUrl + "/api/popular-searches";
        return restTemplate.getForObject(url, List.class);
    }
}
