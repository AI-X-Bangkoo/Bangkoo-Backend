package com.bangkoo.back.DTO.product;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductsResponseDTO {

    /**
     * 클라이언트가 받아야 할 응답 필드 제공
     */
    private String id;      //몽고DB에 저장된 제품의 번호
    private String name;    //제품명
    private LocalDateTime createdAt; //등록일
}
