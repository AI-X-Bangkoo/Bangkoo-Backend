package com.bangkoo.back.service.product;

import com.bangkoo.back.dto.product.ProductsResponseDTO;
import com.bangkoo.back.model.product.Product;
import com.bangkoo.back.repository.product.ProductRepository;
import com.bangkoo.back.service.embedding.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
/**
 * ProductService 클래스는 제품(Product) 데이터를 저장, 수정, 삭제, 조회하는 서비스 로직을 담당합니다.
 */
public class ProductService {

    private final EmbeddingService embeddingService;            //임베딩 서비스 추가
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);  // Logger 객체 추가

    @Autowired
    private ProductRepository productRepository;

    /**
     * 새로운 제품을 저장합니다. 저장 시 생성일(createdAt)을 현재 시간으로 설정합니다.
     * @param product 저장할 제품 객체
     * @return 저장된 제품 객체
     */
    public Product save(Product product){
        if(product.getName() == null || product.getImageUrl() == null){
            logger.error("제품명과 이미지 URL은 필수입니다.");  // 로그 출력
            throw new IllegalArgumentException("제품명과 이미지 URL은 필수입니다.");
        }

        // 이미지 URL을 바탕으로 이미지 임베딩을 생성
        List<Double> imageEmbedding = embeddingService.generateImageEmbedding(product.getImageUrl());
        product.setImageEmbedding(imageEmbedding);  // Product 객체에 이미지 임베딩 값을 설정

        // 텍스트(상세설명, description 등)을 바탕으로 텍스트 임베딩을 생성
        List<Double> textEmbedding = embeddingService.generateTextEmbedding(product.getDescription());
        product.setTextEmbedding(textEmbedding);  // Product 객체에 텍스트 임베딩 값을 설정

        product.setCreatedAt(LocalDateTime.now());
        logger.info("새로운 제품 저장: {}", product.getName());  // 로그 출력
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
            product.setId(updated.getId());
            product.setName(updated.getName());
            product.setDescription(updated.getDescription());
            product.setModel3dUrl(updated.getModel3dUrl());
            product.setDetail(updated.getDetail());
            product.setPrice(updated.getPrice());
            product.setLink(updated.getLink());
            product.setImageUrl(updated.getImageUrl());


            logger.info("제품 수정: {}", product.getName());  // 로그 출력
            return productRepository.save(product);
        } else {
            logger.error("제품을 찾지 못 했습니다. ID: {}", id);  // 로그 출력
            throw new RuntimeException("제품을 찾지 못 했습니다.");
        }
    }

    /**
     * 제품 ID를 기반으로 제품을 삭제합니다.
     * @param id 삭제할 제품의 ID
     */
    public void delete(String id) {
        if(!productRepository.existsById(id)){
            logger.error("해당 제품은 존재하지 않습니다. ID: {}", id);  // 로그 출력
            throw new RuntimeException("해당 제품은 존재하지 않습니다.");
        }
        logger.info("제품 삭제: ID {}", id);  // 로그 출력
        productRepository.deleteById(id);
    }

    /**
     * 모든 제품 목록을 조회합니다.
     * @param page 조회할 페이지 번호
     * @param size 페이지당 출력할 제품 수
     * @return 페이징된 제품 리스트
     */
    public Page<Product> findAll(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> result = productRepository.findAll(pageable);
        return result;
    }


    /**
     * 제품 ID로 단일 제품을 조회합니다.
     * @param id 조회할 제품의 ID
     * @return 조회된 제품 객체
     */
    public Product findById(String id){
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    return new RuntimeException("제품을 찾지 못 했습니다.");
                });
    }

    /**
     * 모든 제품을 가져와서
     * DTO로 변환해서 리스트로 만들기
     */
    public List<ProductsResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> {
            ProductsResponseDTO dto = new ProductsResponseDTO();
            dto.setId(product.getId());
            dto.setDescription(product.getDescription());
            dto.setLink(product.getLink());
            dto.setImageUrl(product.getImageUrl());
            dto.setName(product.getName());
            dto.setCreatedAt(product.getCreatedAt());
            return dto;
        }).toList();
    }

    /**
     * 검색 관련

     * @param page 페이지 번호
     * @param size 페이지당 데이터 개수
     * @return 검색된 제품 리스트 (페이징 적용)
     */
    public Page<Product> searchByKeyword(String search, int page, int size) {
        // Pageable 생성
        PageRequest pageable = PageRequest.of(page, size);

        // ProductRepository의 searchByKeyword 호출
        return productRepository.searchByKeyword(search, pageable);

    }


}
