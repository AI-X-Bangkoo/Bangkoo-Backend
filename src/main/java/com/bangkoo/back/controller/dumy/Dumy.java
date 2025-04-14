package com.bangkoo.back.controller.dumy;

import com.bangkoo.back.model.product.Product;
import com.bangkoo.back.repository.product.ProductRepository;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * 로컬에서 테스트 용으로 만들어둔 더미 데이터 생성기
 */
@RestController
@RequestMapping("/api/products")
public class Dumy {
    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/dummy")
    public String createDummyData() {
        List<Product> dummyList = new ArrayList<>();

        for (int i = 1; i <= 30; i++) {
            Product p = new Product();
            p.setName("가구 " + i);
            p.setDescription("이것은 테스트용 더미 가구입니다. 번호: " + i);
            p.setImageUrl("https://via.placeholder.com/100x100?text=Gagu+" + i);
            p.setCreatedAt(java.time.LocalDateTime.parse(LocalDateTime.now().toString()));
            dummyList.add(p);
        }

        productRepository.saveAll(dummyList);
        return "더미 가구 30개가 성공적으로 추가되었습니다.";
    }
}


