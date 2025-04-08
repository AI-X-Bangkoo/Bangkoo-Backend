package com.bangkoo.back.service.search_none;

import com.bangkoo.back.model.search.SearchQuery;
import com.bangkoo.back.repository.search.SearchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 최초 작성자: 김동규
 * 최초 작성일: 2025-04-03
 *
 * 검색어 로그 기록 및 조회 서비스
 *
 * - 사용자의 검색어를 MongoDB에 저장하거나,
 *   최근 검색어 / 인기 검색어 데이터를 조회하는 역할을 수행
 */
@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final SearchQueryRepository repository;

    /**
     * 검색어를 MongoDB에 저장
     *
     * @param query 검색어
     * @param userId 사용자 ID
     */
    public void logSearch(String query, String userId) {
        SearchQuery log = new SearchQuery();
        log.setQuery(query);
        log.setUserId(userId);
        log.setTimestamp(LocalDateTime.now());
        repository.save(log);
    }

    /**
     * 해당 사용자의 최근 검색어 10개 조회
     *
     * @param userId 사용자 ID
     * @return 최근 검색어 리스트
     */
    public List<SearchQuery> getRecentSearches(String userId) {
        return repository.findTop10ByUserIdOrderByTimestampDesc(userId);
    }

    /**
     * 전체 사용자 기준 인기 검색어 TOP 10 조회
     *
     * @return 인기 검색어 리스트
     */
    public List<String> getPopularSearches() {
        return repository.findTop10PopularQuery();
    }
}
