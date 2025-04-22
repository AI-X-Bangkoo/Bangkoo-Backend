package com.bangkoo.back.service.autoRecommend;

import com.bangkoo.back.model.product.Product;
import com.bangkoo.back.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutoRecommendService {

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    // FastAPI AI 서버 주소
    private static final String AI_IMAGE_EMBEDDING_URL = "http://localhost:8000/api/style-recommendation";

    /**
     * AI 임베딩 + 스타일별 추천
     *
     * @param imageBytes 이미지 파일 바이트
     * @param category   사용자 선택한 가구 카테고리
     * @return 스타일별 2개씩 추천된 제품 리스트
     */
    public List<Product> recommendProductsFromAI(byte[] imageBytes, String category) {
        // 1. AI 서버에 이미지 바이트 전달 → 이미지 임베딩 추출
        float[] imageEmbedding = requestImageEmbedding(imageBytes);

        // 2. 스타일별로 2개씩 추천
        List<Product> recommendedProducts = new ArrayList<>();
        Set<String> processedStyles = new HashSet<>();

        // 3. 카테고리 내 모든 제품을 가져오지 않고, 스타일별로 2개씩 가져오기
        List<Product> allProducts = productRepository.findByCategory(category);

        for (Product product : allProducts) {
            // detail 객체에서 스타일 정보를 추출
            String style = extractStyleFromDetail(product.getDetail());

            // 이미 해당 스타일이 추가된 경우 넘어감
            if (processedStyles.contains(style)) {
                continue;
            }

            // 해당 스타일에 맞는 제품 2개 가져오기
            List<Product> styleProducts = allProducts.stream()
                    .filter(p -> extractStyleFromDetail(p.getDetail()).equals(style))  // detail에서 스타일 비교
                    .limit(2)  // 스타일당 2개만 추천
                    .collect(Collectors.toList());

            recommendedProducts.addAll(styleProducts);
            processedStyles.add(style);  // 처리된 스타일은 다시 추천하지 않음
        }

        return recommendedProducts;
    }

    /**
     * FastAPI 서버로부터 이미지 임베딩 추출 요청
     */
    private float[] requestImageEmbedding(byte[] imageBytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(imageBytes, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                AI_IMAGE_EMBEDDING_URL, requestEntity, String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("AI 서버로부터 임베딩을 받지 못했습니다.");
        }

        // Jackson ObjectMapper를 이용하여 JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode embeddingNode = jsonNode.get("embedding");

            // 임베딩 데이터를 리스트로 변환
            List<Double> doubleList = new ArrayList<>();
            embeddingNode.forEach(node -> doubleList.add(node.asDouble()));

            // float 배열로 변환
            float[] embedding = new float[doubleList.size()];
            for (int i = 0; i < doubleList.size(); i++) {
                embedding[i] = doubleList.get(i).floatValue();
            }

            return embedding;
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * detail 객체에서 스타일 정보를 추출
     */
    private String extractStyleFromDetail(String detail) {
        // "스타일 정보:" 뒤의 내용을 추출
        String stylePrefix = "스타일 정보:";
        int styleIndex = detail.indexOf(stylePrefix);

        if (styleIndex == -1) {
            return "기타";  // 스타일 정보가 없으면 기타로 처리
        }

        // 스타일 정보 이후 부분만 추출
        String styleInfo = detail.substring(styleIndex + stylePrefix.length()).trim();

        // 스타일 정보 중 첫 문장만 추출 (줄바꿈이 있을 경우 첫 문장을 사용)
        int endOfStyleInfo = styleInfo.indexOf("\n");
        if (endOfStyleInfo != -1) {
            styleInfo = styleInfo.substring(0, endOfStyleInfo).trim();
        }

        return styleInfo;
    }
}
