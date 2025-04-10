package com.bangkoo.back.repository.product;

import com.bangkoo.back.model.product.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/**
 * 선언만 하면 기본적인  find id,
 */
public interface ProductRepository extends MongoRepository<Product,String> {
    List<Product> findAllByTemp(boolean isTemp);
}
