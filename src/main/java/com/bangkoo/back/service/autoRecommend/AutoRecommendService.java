package com.bangkoo.back.service.autoRecommend;

import com.bangkoo.back.model.product.Product;
import com.bangkoo.back.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AutoRecommendService {

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    // FastAPI AI 서버 주소 및 방 분석 엔드포인트
    private static final String AI_ANALYZE_ROOM_URL = "http://localhost:8000/api/analyze-room";

    /**
     * FastAPI 서버로부터 방 스타일 분석 결과 요청
     */
    public Map<String, Object> requestRoomAnalysis(byte[] imageBytes) {
        System.out.println("백엔드: AI 서버로 이미지 전송 시작..."); // 추가된 시스템 아웃 프린트라인

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(createMultipartBody(imageBytes), headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                AI_ANALYZE_ROOM_URL, requestEntity, Map.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            System.err.println("백엔드: AI 서버로부터 분석 결과를 받지 못했습니다. 상태 코드: " + response.getStatusCode()); // 에러 시 로그
            throw new RuntimeException("AI 서버로부터 분석 결과를 받지 못했습니다.");
        }

        System.out.println("백엔드: AI 서버로부터 분석 결과 수신 완료."); // 추가된 시스템 아웃 프린트라인

        return response.getBody();
    }

    private MultiValueMap<String, Object> createMultipartBody(byte[] imageBytes) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return "image.jpg"; // 파일 이름 설정 (임의)
            }
        });
        return body;
    }

    /**
     * AI 분석 결과를 기반으로 가구 추천
     *
     * @param aiAnalysisResult AI 서버로부터 받은 분석 결과 (스타일 정보 등)
     * @return 스타일별 2개씩 추천된 제품 리스트
     */
    public List<Product> recommendProductsFromAIAnalysis(Map<String, Object> aiAnalysisResult) {
        String roomStyle = (String) aiAnalysisResult.getOrDefault("style", "unknown");
        // AI 분석 결과에서 필요한 정보 추출 (예: 색상, 재질 등)
        // List<String> colorPalette = (List<String>) aiAnalysisResult.getOrDefault("color_palette", Collections.emptyList());
        // List<String> materials = (List<String>) aiAnalysisResult.getOrDefault("materials", Collections.emptyList());

        // 현재는 모든 카테고리의 상품을 대상으로 추천하고 있습니다.
        // 특정 카테고리의 상품만 추천하려면 컨트롤러에서 전달받거나,
        // 이 메서드의 파라미터로 카테고리를 추가해야 합니다.
        List<Product> allProducts = productRepository.findAll(); // 모든 상품 조회
        List<Product> recommendedProducts = new ArrayList<>();
        Set<String> processedStyles = new HashSet<>();

        for (Product product : allProducts) {
            String productStyle = extractStyleFromDetail(product.getDetail());
            if (processedStyles.contains(productStyle)) {
                continue;
            }

            // 간단한 스타일 기반 필터링
            if (productStyle.contains(roomStyle) || roomStyle.contains(productStyle)) {
                recommendedProducts.add(product);
                processedStyles.add(productStyle);
                if (processedStyles.size() >= 2) { // 예시: 최대 스타일별 2개
                    break;
                }
            }
        }

        return recommendedProducts;
    }

    /**
     * detail 객체에서 스타일 정보를 추출 (기존 코드 유지)
     */
    private String extractStyleFromDetail(String detail) {
        String stylePrefix = "스타일 정보:";
        int styleIndex = detail.indexOf(stylePrefix);

        if (styleIndex == -1) {
            return "기타";
        }

        String styleInfo = detail.substring(styleIndex + stylePrefix.length()).trim();
        int endOfStyleInfo = styleInfo.indexOf("\n");
        if (endOfStyleInfo != -1) {
            styleInfo = styleInfo.substring(0, endOfStyleInfo).trim();
        }

        return styleInfo;
    }
}