package com.bangkoo.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 최초 작성자 : 김동규
 * 최초 작성일 : 2025-04-01
 *
 * 웹 관련 설정 클래스
 * - CORS 정책 설정
 * - 프론트엔드와의 통신 허용을 위한 전역 설정
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * CORS 전역 설정
     * - 모든 경로에 대해 모든 Origin, Method, Header 허용
     * - 개발 단계에서 편의를 위해 전체 허용
     *
     * @param registry CORS 매핑 등록용 객체
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}
