package com.bangkoo.back.controller.product;

import com.bangkoo.back.model.product.Product;
import com.bangkoo.back.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
/**
 * 제품의 추가, 수정, 삭제, 조회, 임시 저장 등
 * 클라이언트 요청을 받아 ProductService로 전달하는 컨트롤러 클래스입니다.
 */
public class ProductController {

    private final ProductService productService;

    /**
     * 일반 제품 저장 API
     * POST /product/save
     * @param product 저장할 제품 정보
     * @return 저장된 제품
     */
    @PostMapping("/save")
    public Product saveProduct(@RequestBody Product product) {
        product.setTemp(false);
        return productService.save(product);
    }

    /**
     * 임시 제품 저장 API
     * POST /product/temp/save
     * @param product 임시 저장할 제품 정보
     * @return 저장된 제품
     */
    @PostMapping("/temp/save")
    public Product saveTempProduct(@RequestBody Product product) {
        product.setTemp(true);
        return productService.save(product);
    }

    /**
     * 제품 수정 API
     * PUT /product/{id}
     * @param id 수정할 제품 ID
     * @param product 수정할 내용이 담긴 제품 객체
     * @return 수정된 제품
     */
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    /**
     * 제품 삭제 API
     * DELETE /product/{id}
     * @param id 삭제할 제품 ID
     */
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.delete(id);
    }

    /**
     * 전체 제품 조회 API
     * GET /product
     * @return 모든 제품 리스트
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    /**
     * 단일 제품 조회 API
     * GET /product/{id}
     * @param id 조회할 제품 ID
     * @return 해당 제품 객체
     */
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable String id){
        return productService.findById(id);
    }

    /**
     * 임시 저장된 제품만 조회 API
     * GET /product/temp
     * @return 임시 저장된 제품 리스트
     */
    @GetMapping("/temp")
    public List<Product> getTempProducts(){
        return productService.findAllByIsTemp(true);
    }
}
