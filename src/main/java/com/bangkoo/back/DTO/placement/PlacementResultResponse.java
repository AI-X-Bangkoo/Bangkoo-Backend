package com.bangkoo.back.dto.placement;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PlacementResultResponse {
    private String imageUrl;
    private LocalDateTime createdAt;
}
