package com.bangkoo.back.model.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 최초 작성자: 김동규
 * 최초 작성일: 2025-04-03
 *
 * 검색어 로그 도큐먼트 모델
 *
 * - 사용자가 검색한 쿼리를 MongoDB에 저장하기 위한 모델
 * - 컬렉션 이름: search_queries
 *
 * 필드 설명:
 * - id: MongoDB에서 자동 생성되는 ObjectId (문자열 형태)
 * - query: 사용자가 입력한 검색어
 * - timestamp: 검색이 발생한 시각
 * - userId: 검색을 수행한 사용자 ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "search_queries")
public class SearchQuery {

    /** MongoDB ObjectId (자동 생성) */
    @Id
    private String id;

    /** 사용자 검색어 */
    private String query;

    /** 검색 시각 */
    private LocalDateTime timestamp;

    /** 사용자 ID */
    private String userId;
}
