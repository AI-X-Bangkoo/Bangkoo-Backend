package com.bangkoo.back.repository.placement;

import com.bangkoo.back.model.placement.PlacementResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PlacementResultRepository extends MongoRepository<PlacementResult, String> {
    List<PlacementResult> findByUserId(String userId);
}
