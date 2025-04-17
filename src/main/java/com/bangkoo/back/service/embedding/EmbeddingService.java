package com.bangkoo.back.service.embedding;

import com.bangkoo.back.dto.embedding.EmbeddingRequestDTO;
import com.bangkoo.back.model.product.Product;
import com.bangkoo.back.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class EmbeddingService {

    @Value("${ai.server.url}")
    private String aiBaseUrl;

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    public EmbeddingService(RestTemplate restTemplate, ProductRepository productRepository) {
        this.restTemplate = restTemplate;
        this.productRepository = productRepository;
    }

    /**
     * 이미지 URL을 바탕으로 임베딩 생성
     */
    public List<Double> generateImageEmbedding(String imageUrl) {
        // 이미지 임베딩을 List<Double> 형태로 반환
        return restTemplate.postForObject(aiBaseUrl + "/generate-image-embedding", imageUrl, List.class);
    }

    /**
     * 텍스트를 바탕으로 임베딩 생성
     */
    public List<Double> generateTextEmbedding(String text) {
        // 텍스트 임베딩을 List<Double> 형태로 반환
        return restTemplate.postForObject(aiBaseUrl + "/generate-text-embedding", text, List.class);
    }

    /**
     * 여러 제품 리스트를 받아서 임베딩 생성 후 DB에 저장
     */
    public void embeddingSaveProduct(List<EmbeddingRequestDTO> products) {
        for (EmbeddingRequestDTO dto : products) {
            // 1. 이미지 임베딩
            List<Double> imageEmbedding = generateImageEmbedding(dto.getImageUrl());

            // 2. 텍스트 임베딩 (description + detail)
            String combinedText = dto.getDescription() + " " + dto.getDetail();
            List<Double> textEmbedding = generateTextEmbedding(combinedText);

            // 3. 평균 임베딩
            List<Double> combinedEmbedding = averageEmbedding(imageEmbedding, textEmbedding);

            // 4. Product 저장
            Product product = Product.builder()
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .detail(dto.getDetail())
                    .imageUrl(dto.getImageUrl())
                    .link(dto.getLink())
                    .imageEmbedding(imageEmbedding)
                    .textEmbedding(textEmbedding)
                    .combinedEmbedding(combinedEmbedding)
                    .createdAt(LocalDateTime.now())
                    .build();

            productRepository.save(product);
        }
    }

    /**
     * 평균 임베딩 벡터 계산
     */
    private List<Double> averageEmbedding(List<Double> a, List<Double> b) {
        int size = Math.min(a.size(), b.size());
        return IntStream.range(0, size)
                .mapToObj(i -> (a.get(i) + b.get(i)) / 2.0)
                .toList();
    }
}
