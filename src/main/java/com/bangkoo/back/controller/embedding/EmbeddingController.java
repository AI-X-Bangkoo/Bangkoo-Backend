package com.bangkoo.back.controller.embedding;

import com.bangkoo.back.dto.embedding.EmbeddingListRequestDTO;
import com.bangkoo.back.service.embedding.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 최초 작성자: 김병훈
 * 최초 작성일: 2025-04-17
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    @PostMapping("/embedding")
    public ResponseEntity<String> uploadEmbeddings(@RequestBody EmbeddingListRequestDTO requestDTO) {
        embeddingService.embeddingSaveProduct(requestDTO.getProducts());
        return ResponseEntity.ok("임베딩 및 저장 완료");
    }
}
