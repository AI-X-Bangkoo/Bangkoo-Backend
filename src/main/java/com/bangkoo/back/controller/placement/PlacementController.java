package com.bangkoo.back.controller.placement;

import com.bangkoo.back.dto.placement.PlacementResultResponse;
import com.bangkoo.back.service.placement.PlacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 최초 작성자: 김태원
 * 최초 작성일: 2025-04-08
 *
 * 가구 배치(AI) 요청 컨트롤러
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlacementController {

    private final PlacementService placementService;

    /**
     * 사용자 이미지 기반 AI 배치 생성 요청
     *
     * @param mode        작업 타입 (add/remove 등)
     * @param background  배경 이미지
     * @param reference   참고 이미지 (add일 경우)
     * @return 결과 메시지 or 이미지 처리 결과
     */
    @PostMapping(value = "/placement", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> generatePlacement(
            @RequestParam String mode,
            @RequestParam MultipartFile background,
            @RequestParam(required = false) MultipartFile reference
    ) throws IOException {
        String base64 = placementService.sendToAiServer(mode, background, reference);
        return ResponseEntity.ok(base64);
    }

    /**
     * 결과 이미지 저장 (S3 업로드 + Mongo 저장)
     */
    @PostMapping(value = "/placement/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> savePlacementImage(
            @RequestParam MultipartFile file,
            @RequestParam String userId
    ) throws IOException {
        // 파일 → S3 업로드 → URL 리턴
        String imageUrl = placementService.uploadAndSaveResult(file, userId);
        return ResponseEntity.ok(Map.of("image_url", imageUrl));
    }

    @GetMapping("/placement/results")
    public ResponseEntity<?> getMyPlacements(@RequestParam String userId) {
        List<PlacementResultResponse> results = placementService.getResultsByUser(userId);
        return ResponseEntity.ok(results);
    }
}
