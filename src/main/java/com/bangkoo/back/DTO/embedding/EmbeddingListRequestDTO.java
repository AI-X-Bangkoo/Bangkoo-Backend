package com.bangkoo.back.dto.embedding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingListRequestDTO {
    private List<EmbeddingRequestDTO> products;
}
