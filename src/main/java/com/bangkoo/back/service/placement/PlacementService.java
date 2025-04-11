package com.bangkoo.back.service.placement;

import com.bangkoo.back.dto.placement.PlacementResultResponse;
import com.bangkoo.back.model.placement.PlacementResult;
import com.bangkoo.back.repository.placement.PlacementResultRepository;
import com.bangkoo.back.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 최초 작성자 : 김태원
 * 최초 작성일 : 2025-04-11
 *
 * 🧠 PlacementService
 * - AI 서버 요청 및 결과 저장 로직 담당
 */
@Service
@RequiredArgsConstructor
public class PlacementService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final S3Uploader s3Uploader;
    private final PlacementResultRepository placementResultRepository;

    /**
     * application.yml에 정의된 ai.server.url 값을 주입받음
     * 예: http://localhost:8000/api
     */
    @Value("${ai.server.url}")
    private String aiBaseUrl;

    /**
     * AI 서버로 배치 요청 (mode, background, reference 이미지 전송)
     */
    public String sendToAiServer(String mode, MultipartFile background, MultipartFile reference) throws IOException {
        String aiUrl = aiBaseUrl + "/placement";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("mode", mode);
        body.add("background", convertToResource(background));
        if ("add".equals(mode) && reference != null) {
            body.add("reference", convertToResource(reference));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(aiUrl, requestEntity, Map.class);

        Map responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("image_base64")) {
            throw new RuntimeException("AI 서버 응답이 유효하지 않음.");
        }

        return (String) responseBody.get("image_base64");
    }

    /**
     * MultipartFile을 ByteArrayResource로 변환
     */
    private Resource convertToResource(MultipartFile file) throws IOException {
        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
    }

    /**
     * S3에 업로드하고, placement_results 컬렉션에 저장
     */
    public String uploadAndSaveResult(MultipartFile file, String userId) throws IOException {
        String imageUrl = s3Uploader.upload(file, "img");

        PlacementResult result = PlacementResult.builder()
                .userId(userId)
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .build();

        placementResultRepository.save(result);
        return imageUrl;
    }

    public List<PlacementResultResponse> getResultsByUser(String userId) {
        return placementResultRepository.findByUserId(userId).stream()
                .map(result -> PlacementResultResponse.builder()
                        .imageUrl(result.getImageUrl())
                        .createdAt(result.getCreatedAt())
                        .build())
                .toList();
    }
}
