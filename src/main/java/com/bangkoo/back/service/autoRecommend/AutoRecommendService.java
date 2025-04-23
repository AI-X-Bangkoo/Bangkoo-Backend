package com.bangkoo.back.service.autoRecommend;

import com.bangkoo.back.dto.product.ProductsResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.core.io.ByteArrayResource;
import com.bangkoo.back.model.product.Product;

import java.util.List;
import java.util.Map;

@Service
public class AutoRecommendService {

    @Value("${ai.server.url}")  // AI 서버의 URL을 가져옵니다.
    private String aiServerUrl;

    private final RestTemplate restTemplate;

    public AutoRecommendService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // AI 서버로 이미지를 전송하고 분석 결과를 받아옵니다
    public Map<String, Object> sendImageToAI(MultipartFile file) {
        try {
            // AI 서버로 전송할 이미지 파일을 담을 HttpEntity 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // MultipartFile을 byte 배열로 변환
            byte[] fileBytes = file.getBytes();

            // ByteArrayResource를 사용하여 MultipartFile 전송
            ByteArrayResource resource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();  // 파일명 설정
                }
            };

            // MultipartFile을 포함할 FormData 구성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            // HttpEntity에 헤더와 바디 설정
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            // AI 서버에 요청 보내기 (AI 서버에서 Gemini로 이미지 분석을 요청합니다)
            ResponseEntity<Map> response = restTemplate.exchange(
                    aiServerUrl + "/room-analysis",  // AI 서버의 URL
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // AI 서버에서 받은 응답 반환 (Gemini 호출 후 결과 처리)
            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("이미지 전송 실패: " + e.getMessage());
        }
    }

    // AI 분석 결과를 기반으로 추천된 가구를 반환하는 메서드
    public List<ProductsResponseDTO> recommendProductsFromAIAnalysis(Map<String, Object> aiAnalysisResult) {
        // 여기에 분석된 데이터를 기반으로 가구 추천 로직을 작성
        // 예시로, AI 분석 결과에서 특정 키를 추출하고, 그것을 기반으로 추천된 가구 리스트를 반환합니다
        // 실제 로직에 맞게 수정해야 합니다

        // 예시 반환
        return List.of(new ProductsResponseDTO(), new ProductsResponseDTO());
    }
}
