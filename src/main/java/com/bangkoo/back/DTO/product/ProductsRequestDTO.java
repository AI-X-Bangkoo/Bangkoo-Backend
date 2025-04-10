package com.bangkoo.back.DTO.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductsRequestDTO {
    /**
     * 프론트에서 요청 받을 필드 정의
     */

    private String name;           // 제품명
    private String description;    // 간단 설명
    private String price;          // 가격 (₩단위 포함 또는 int)
    private String link;           // IKEA 상세 링크
    private String imageUrl;       // 대표 이미지 URL
    private String model3dUrl;     // 3D 이미지 URL(태원)

}
