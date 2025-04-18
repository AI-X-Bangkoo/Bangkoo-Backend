package com.bangkoo.back.repository.product;


import com.bangkoo.back.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    /**
     * name, description, id로 검색
     *
     * @param search
     * @param pageable
     * @return
     */
    @Query("{'$or': [{'name': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}}, {'id': {$regex: ?0, $options: 'i'}}]}")
    Page<Product> searchByKeyword(String search, Pageable pageable);

    // 전체 제품 조회 (페이징 처리 포함)
    Page<Product> findAll(Pageable pageable);

}
