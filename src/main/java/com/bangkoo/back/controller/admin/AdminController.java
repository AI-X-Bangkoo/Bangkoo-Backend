package com.bangkoo.back.controller.admin;


import com.bangkoo.back.dto.product.ProductsResponseDTO;
import com.bangkoo.back.repository.product.ProductRepository;
import com.bangkoo.back.service.product.ProductService;
import com.bangkoo.back.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final JwtUtil jwtUtil;


    public AdminController(ProductService productService, ProductRepository productRepository, JwtUtil jwtUtil) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.jwtUtil = jwtUtil;
    }

    // 관리자 인증 후 전체 상품 조회
    @GetMapping("/products")
    public List<ProductsResponseDTO> findAllProducts(HttpServletRequest request) {
        String token = jwtUtil.getJwtFromRequest(request);
        if (token == null || !jwtUtil.isValidToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        String role = jwtUtil.getUserRoleFromToken(token);
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("관리자만 접근할 수 있습니다.");
        }

        return productService.getAllProducts();
    }
}
