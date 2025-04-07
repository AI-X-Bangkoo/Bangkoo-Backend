package com.bangkoo.back.config;

import com.bangkoo.back.config.properites.SocialOAuthProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SocialOAuthProperties.class)
public class AppConfig {
    //설정 등록만 담당. 코드 X
}
