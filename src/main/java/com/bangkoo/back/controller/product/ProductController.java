package com.bangkoo.back.controller.product;

import com.bangkoo.back.dto.product.ProductsRequestDTO;
import com.bangkoo.back.dto.product.ProductsResponseDTO;
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
     * JWT 토큰을 헤더에서 추출하고 유효성을 검증하여 이메일을 반환하는 메서드
     */
    private String extractEmailFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtUtil.isValidToken(token)) {
                return jwtUtil.getEmailFromToken(token);
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
    }

    /**
     * 일반 제품 저장 API
     * POST /product/save
     * @param requestDTO 저장할 제품 정보
     * @return 저장된 제품
     */

    @PostMapping("/product/save")
    public ProductsResponseDTO saveProduct(@RequestBody ProductsRequestDTO requestDTO, HttpServletRequest request) {
        // JWT 토큰에서 이메일 추출
        String username = extractEmailFromRequest(request);

        // ProductsRequestDTO -> Product 엔티티로 변환
        Product product = dtoMapper.toEntity(requestDTO);
        // 저장 시 사용자 이메일이나 추가 정보를 사용할 수 있음
        // product.setOwnerEmail(username); 예시로 이메일 저장 가능

        Product saved = productService.save(product);
        return dtoMapper.toResponseDTO(saved);
    }

    /**
     * 제품 수정 API
     * PUT /product/{id}
     * @param id 수정할 제품 ID
     * @param requestDTO 수정할 내용이 담긴 제품 DTO
     * @return 수정된 제품
     */
    @PutMapping("/product/{id}")
    public ProductsResponseDTO updateProduct(@PathVariable String id, @RequestBody ProductsRequestDTO requestDTO) {
        Product product = dtoMapper.toEntity(requestDTO);
        Product updated = productService.update(id, product);
        return dtoMapper.toResponseDTO(updated);
    }

    /**
     * 제품 삭제 API
     * DELETE /product/{id}
     * @param id 삭제할 제품 ID
     */
    @DeleteMapping("/product/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.delete(id);
    }

    /**
     * 전체 제품 조회 API
     * GET /product
     * @param page 페이지 번호
     * @param size 페이지 당 개수
     * @return 제품 목록
     */
    @GetMapping("/product")
    public List<ProductsResponseDTO> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Page<Product> productPage = productService.findAll(page, size);
        return productPage.map(dtoMapper::toResponseDTO).getContent(); // 페이징된 DTO 반환
    }

    /**
     * 단일 제품 조회 API
     * GET /product/{id}
     * @param id 조회할 제품 ID
     * @return 해당 제품 DTO
     */
    @GetMapping("/product/{id}")
    public ProductsResponseDTO getProduct(@PathVariable String id) {
        Product product = productService.findById(id);
        return dtoMapper.toResponseDTO(product);
    }
}

