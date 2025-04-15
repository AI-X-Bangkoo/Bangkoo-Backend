package com.bangkoo.back.controller.product;

import com.bangkoo.back.dto.product.ProductsRequestDTO;
import com.bangkoo.back.dto.product.ProductsResponseDTO;
import com.bangkoo.back.mapper.ProductDtoMapper;
import com.bangkoo.back.model.product.Product;
import com.bangkoo.back.service.product.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")  // 관리자 페이지 관련 API는 /api/admin으로 변경
public class ProductController {

    private final ProductService productService;
    private final ProductDtoMapper dtoMapper;

    public ProductController(ProductService productService, ProductDtoMapper dtoMapper) {
        this.productService = productService;
        this.dtoMapper = dtoMapper;
    }

    /**
     * 제품 저장 API
     */
    @PostMapping("/product/save")
    public ProductsResponseDTO saveProduct(@RequestBody ProductsRequestDTO requestDTO) {
        Product product = dtoMapper.toEntity(requestDTO);
        Product saved = productService.save(product);
        return dtoMapper.toResponseDTO(saved);
    }

    /**
     * 제품 수정 API
     */
    @PutMapping("/product/{id}")
    public ProductsResponseDTO updateProduct(@PathVariable String id, @RequestBody ProductsRequestDTO requestDTO) {
        Product product = dtoMapper.toEntity(requestDTO);
        Product updated = productService.update(id, product);
        return dtoMapper.toResponseDTO(updated);
    }

    /**
     * 제품 삭제 API
     */
    @DeleteMapping("/product/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.delete(id);
    }

    /**
     * 전체 제품 조회 API (페이징)
     */
    @GetMapping("/product")
    public List<ProductsResponseDTO> getAllProducts(@RequestParam(name = "page") int page,
                                                    @RequestParam(name = "size") int size) {
        Page<Product> productPage = productService.findAll(page, size);


        return productPage.map(dtoMapper::toResponseDTO).getContent();
    }


    /**
     * 단일 제품 조회 API
     */
    @GetMapping("/product/{id}")
    public ProductsResponseDTO getProduct(@PathVariable String id) {
        Product product = productService.findById(id);
        return dtoMapper.toResponseDTO(product);
    }
}
