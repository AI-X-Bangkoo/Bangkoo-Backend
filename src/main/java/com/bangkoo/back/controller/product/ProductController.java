package com.bangkoo.back.controller.product;

import com.bangkoo.back.DTO.product.ProductsRequestDTO;
import com.bangkoo.back.DTO.product.ProductsResponseDTO;
import com.bangkoo.back.mapper.ProductDtoMapper;
import com.bangkoo.back.model.product.Product;
import com.bangkoo.back.service.product.ProductService;
import com.bangkoo.back.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final JwtUtil jwtUtil;
    private final ProductDtoMapper dtoMapper;

    /**
     * 일반 제품 저장 API
     * POST /product/save
     * @param product 저장할 제품 정보
     * @return 저장된 제품
     */
    @PostMapping("/save")
    public ProductsResponseDTO saveProduct(@RequestBody ProductsRequestDTO requestDTO, HttpServletRequest request) {

        //JWT 토큰을 헤더에서 추출
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);         //"bearer" 부분을 제거한 토큰만 추출
        }

        if (token != null && jwtUtil.isValidToken(token)) {
            // JWT가 유효하다면 사용자 정보 추출
            String username = jwtUtil.getEmailFromToken(token);
            // 사용자 이름 또는 다른 정보로 비즈니스 로직을 처리 (예: 제품 소유자 정보 추가)
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid or expired token");
        }

        Product product = dtoMapper.toEntity(requestDTO);
        product.setTemp(false);
        Product saved = productService.save(product);
        return dtoMapper.toResponseDTO(saved);
    }

    /**
     * 임시 제품 저장 API
     * POST /product/temp/save
     * @param product 임시 저장할 제품 정보
     * @return 저장된 제품
     */
    @PostMapping("/temp/save")
    public ProductsResponseDTO saveTempProduct(@RequestBody Product product) {
        product.setTemp(true);
        Product saved = productService.save(product);
        return dtoMapper.toResponseDTO(saved);
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
    public List<ProductsResponseDTO> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Page<Product> productPage = productService.findAll(page, size);
        return productPage.map(dtoMapper::toResponseDTO).getContent(); // 페이징된 DTO 반환
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
    public List<ProductsResponseDTO> getTempProducts(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Page<Product> productPage = productService.findAllByIsTemp(true, page, size);
        return productPage.map(dtoMapper::toResponseDTO).getContent();
    }
}
