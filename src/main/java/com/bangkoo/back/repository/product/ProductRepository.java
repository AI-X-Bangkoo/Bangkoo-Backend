package com.bangkoo.back.repository.product;

import com.bangkoo.back.dto.product.ProductsResponseDTO;
import com.bangkoo.back.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    // 제품 이름으로 검색
    List<Product> findByName(String name);

    // 전체 제품 조회 (페이징 처리 포함)
    Page<Product> findAll(Pageable pageable);
}
