package com.bangkoo.back.controller.product;

import com.bangkoo.back.dto.product.ProductPageResponseDTO;
import com.bangkoo.back.dto.product.ProductsRequestDTO;
import com.bangkoo.back.dto.product.ProductsResponseDTO;
import com.bangkoo.back.mapper.ProductDtoMapper;
import com.bangkoo.back.model.product.Product;
import com.bangkoo.back.service.product.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ProductsResponseDTO updateProduct(@PathVariable("id") String id, @RequestBody ProductsRequestDTO requestDTO) {

        Product product = dtoMapper.toEntity(requestDTO);
        Product updated = productService.update(id, product);
        return dtoMapper.toResponseDTO(updated);
    }

    /**
     * 제품 삭제 API
     */
    @DeleteMapping("/product/{id}")
    public void deleteProduct(@PathVariable("id") String id) {
        productService.delete(id);
    }

    /**
     * 전체 제품 조회 API (페이징)
     */
    @GetMapping("/product")
    public ProductPageResponseDTO getAllProducts(@RequestParam(name = "page") int page,
                                                 @RequestParam(name = "size") int size) {
        return getProducts(null, page, size);  // 검색 없이 전체 제품 조회
    }

    /**
     * 제품 목록 조회 + 검색 API
     */
    @GetMapping("/products")
    public ProductPageResponseDTO searchProducts(
            @RequestParam(name = "searchTerm", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return getProducts(search, page, size);
    }

    private ProductPageResponseDTO getProducts(String search, int page, int size){
        Page<Product> productPage;


        if (search != null && !search.isEmpty()) {

            productPage = productService.searchByKeyword(search, page, size);
        } else {

            productPage = productService.findAll(page, size);
        }

        List<ProductsResponseDTO> content = productPage.map(dtoMapper::toResponseDTO).getContent();


        content.forEach(product -> System.out.println(product.getName()));  // product.getName()으로 제품 이름 출력

        return new ProductPageResponseDTO(
                content,
                productPage.getTotalPages(),
                productPage.getTotalElements(),
                productPage.getNumber()
        );
    }

}
