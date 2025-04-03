package com.bangkoo.back.service.recommend;

import com.bangkoo.back.utils.MultipartInputStreamFileResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 최초 작성자: 김동규
 * 최초 작성일: 2025-04-03
 *
 * AI 추천 요청 서비스
 *
 * - FastAPI AI 서버로 사용자의 방 사진 + 쿼리를 전송하여 추천 결과를 받아오는 역할
 * - multipart/form-data 형식으로 이미지와 쿼리를 전송함
 */
@Service
public class RecommendService {

    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public RecommendService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * AI 서버에 이미지와 쿼리를 전송하여 추천 결과 반환
     *
     * @param image 방 이미지 파일
     * @param query 사용자의 추천 요청 문장
     * @param minPrice 사용자가 선택한 최소 금액
     * @param maxPrice 사용자가 선택한 최대 금액
     * @param keyword 사용자가 선택한 키워드
     * @param style 사용자가 선택한 스타일
     * @return AI 추천 결과(JSON 문자열)
     * @throws IOException 이미지 읽기 오류 발생 시
     */
    public String recommend(
            MultipartFile image,
            String query,
            Integer minPrice,
            Integer maxPrice,
            String keyword,
            String style
    ) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("query", query);
        body.add("image", new MultipartInputStreamFileResource(image.getInputStream(), image.getOriginalFilename()));

        if (minPrice != null) body.add("min_price", minPrice);
        if (maxPrice != null) body.add("max_price", maxPrice);
        if (keyword != null) body.add("keyword", keyword);
        if (style != null) body.add("style", style);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        String url = aiServerUrl + "/recommend";
        return restTemplate.postForObject(url, request, String.class);
    }

}
