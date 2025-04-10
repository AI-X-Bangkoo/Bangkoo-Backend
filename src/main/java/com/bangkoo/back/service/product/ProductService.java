package com.bangkoo.back.service.product;

import com.bangkoo.back.model.product.Product;
import com.bangkoo.back.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
/**
 * ProductService 클래스는 제품(Product) 데이터를 저장, 수정, 삭제, 조회하는 서비스 로직을 담당합니다.
 */
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 새로운 제품을 저장합니다. 저장 시 생성일(createdAt)을 현재 시간으로 설정합니다.
     * @param product 저장할 제품 객체
     * @return 저장된 제품 객체
     */
    public Product save(Product product){
        product.setCreatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    /**
     * 기존 제품을 수정합니다. 해당 ID로 제품을 찾고, 값들을 업데이트한 후 저장합니다.
     * @param id 수정할 제품의 ID
     * @param updated 업데이트할 제품 정보
     * @return 수정된 제품 객체
     */
    public Product update(String id, Product updated) {
        Optional<Product> existing = productRepository.findById(id);
        if (existing.isPresent()) {
            Product product = existing.get();
            product.setName(updated.getName());
            product.setDescription(updated.getDescription());
            product.setDetail(updated.getDetail());
            product.setPrice(updated.getPrice());
            product.setLink(updated.getLink());
            product.setImageUrl(updated.getImageUrl());
            product.setCsv(updated.getCsv());
            product.setImageEmbedding(updated.getImageEmbedding());
            product.setTextEmbedding(updated.getTextEmbedding());
            return productRepository.save(product);
        } else {
            throw new RuntimeException("제품을 찾지 못 했습니다.");
        }
    }

    /**
     * 제품 ID를 기반으로 제품을 삭제합니다.
     * @param id 삭제할 제품의 ID
     */
    public void delete(String id) {
        productRepository.deleteById(id);
    }

    /**
     * 모든 제품 목록을 조회합니다.
     * @return 제품 리스트
     */
    public List<Product> findAll(){
        return productRepository.findAll();
    }

    /**
     * 제품 ID로 단일 제품을 조회합니다.
     * @param id 조회할 제품의 ID
     * @return 조회된 제품 객체
     */
    public Product findById(String id){
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("제품을 찾지 못 했습니다."));
    }

    /**
     * 임시 데이터 여부(isTemp)에 따라 제품 목록을 조회합니다.
     * @param isTemp 임시 데이터 여부
     * @return 조건에 맞는 제품 리스트
     */
    public List<Product> findAllByIsTemp(boolean isTemp){
        return productRepository.findAllByTemp(isTemp);
    }

}
