package com.bangkoo.back.config.properites;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "kakao")
public class SocialOAuthProperties {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
