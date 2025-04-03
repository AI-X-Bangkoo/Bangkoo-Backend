package com.bangkoo.back.repository.search;

import com.bangkoo.back.model.search.SearchQuery;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 최초 작성자: 김동규
 * 최초 작성일: 2025-04-03
 *
 * 검색어 로그 MongoDB 리포지토리
 *
 * - 사용자 검색어 기록을 MongoDB에서 조회 및 집계하는 역할
 * - 기본 CRUD + 커스텀 쿼리 메서드 포함
 */
@Repository
public interface SearchQueryRepository extends MongoRepository<SearchQuery, String> {

    /**
     * 특정 사용자의 최근 검색어 10개를 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 최근 검색어 목록
     */
    @Query(value = "{ 'userId': ?0 }", sort = "{ 'timestamp': -1 }")
    List<SearchQuery> findTop10ByUserIdOrderByTimestampDesc(String userId);

    /**
     * 인기 검색어 TOP 10 조회 (전체 사용자 기준)
     *
     * - 동일한 쿼리를 그룹핑하여 개수를 집계하고
     *   가장 많이 검색된 10개 쿼리를 반환
     *
     * @return 인기 검색어 문자열 리스트
     */
    @Aggregation(pipeline = {
            "{ '$group': { '_id': '$query', 'count': { '$sum': 1 } } }",
            "{ '$sort': { 'count': -1 } }",
            "{ '$limit': 10 }",
            "{ '$project': { '_id': 0, 'query': '$_id' } }"
    })
    List<String> findTop10PopularQuery();
}
