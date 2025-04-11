package com.bangkoo.back.model.placement;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "placement_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementResult {

    @Id
    private String id;

    private String userId;
    private String imageUrl;
    private LocalDateTime createdAt;

}
