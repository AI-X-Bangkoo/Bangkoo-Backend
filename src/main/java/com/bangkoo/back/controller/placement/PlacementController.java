package com.bangkoo.back.controller.placement;

import com.bangkoo.back.dto.placement.PlacementResultResponse;
import com.bangkoo.back.service.placement.PlacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * 최초 작성자 : 김태원
 * 최초 작성일 : 2025-04-08
 *
 * 🧠 가구 배치(AI) 요청 컨트롤러
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlacementController {

    private final PlacementService placementService;

    /**
     * 🎨 사용자 이미지 기반 AI 배치 생성 요청
     */
    @PostMapping(value = "/placement", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> generatePlacement(
            @RequestParam String mode,
            @RequestParam MultipartFile background,
            @RequestParam(required = false) MultipartFile reference
    ) throws IOException {

        // 요청 검증
        if (mode == null || mode.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "mode는 필수입니다.");
        }

        if (background == null || background.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "배경 이미지가 첨부되지 않았습니다.");
        }

        if (mode.equals("add") && (reference == null || reference.isEmpty())) {
            throw new ResponseStatusException(BAD_REQUEST, "'add' 모드일 경우 reference 이미지는 필수입니다.");
        }

        // 처리 위임
        String base64 = placementService.sendToAiServer(mode, background, reference);
        return ResponseEntity.ok(base64);
    }

    /**
     * 💾 결과 이미지 저장 (S3 업로드 + Mongo 저장)
     */
    @PostMapping(value = "/placement/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> savePlacementImage(
            @RequestParam MultipartFile file,
            @RequestParam String userId,
            @RequestParam String explanation
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "파일이 첨부되지 않았습니다.");
        }

        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "userId는 필수입니다.");
        }

        String imageUrl = placementService.uploadAndSaveResult(file, userId, explanation);
        return ResponseEntity.ok(Map.of("image_url", imageUrl));
    }

    /**
     * 📂 사용자 배치 결과 목록 조회
     */
    @GetMapping("/placement/results")
    public ResponseEntity<?> getMyPlacements(@RequestParam String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "userId는 필수입니다.");
        }

        List<PlacementResultResponse> results = placementService.getResultsByUser(userId);

        if (results.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "저장된 결과가 없습니다.");
        }

        return ResponseEntity.ok(results);
    }
}
