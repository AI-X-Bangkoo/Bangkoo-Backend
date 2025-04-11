package com.bangkoo.back.service.placement;

import com.bangkoo.back.model.placement.PlacementResult;
import com.bangkoo.back.repository.placement.PlacementResultRepository;
import com.bangkoo.back.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PlacementService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final S3Uploader s3Uploader;
    private final PlacementResultRepository placementResultRepository;

    /**
     * AI 서버로 배치 요청 (mode, background, reference 이미지 전송)
     */
    public String sendToAiServer(String mode, MultipartFile background, MultipartFile reference) throws IOException {
        String aiUrl = "http://localhost:8000/api/placement";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("mode", mode);
        body.add("background", convertToResource(background));
        System.out.println(mode);
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
        try {
            return new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        } catch (IOException e) {
            throw new RuntimeException("파일 변환 실패", e);
        }
    }


    /**
     * S3에 업로드하고, placement_results 컬렉션에 저장
     */
    public String uploadAndSaveResult(MultipartFile file, String userId) throws IOException {
        // 1. S3 업로드
        String imageUrl = s3Uploader.upload(file, "img");

        // 2. MongoDB 저장
        PlacementResult result = PlacementResult.builder()
                .userId(userId)
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .build();

        placementResultRepository.save(result);
        return imageUrl;
    }
}
