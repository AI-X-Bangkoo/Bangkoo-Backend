package com.bangkoo.back.repository.product;

import com.bangkoo.back.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * 선언만 하면 기본적인  찾기, 삭제 등등을 사용 가능
 */
public interface ProductRepository extends MongoRepository<Product,String> {
    //임시 저장 조회 (페이징 처리)
    Page<Product> findAllByTemp(boolean isTemp, Pageable pageable);

    //전체 제품 조회(페이징 처리)
    Page<Product> findAll(Pageable pageable);

    //제품 ID로 조회
    Optional<Product> findById(String id);
}
