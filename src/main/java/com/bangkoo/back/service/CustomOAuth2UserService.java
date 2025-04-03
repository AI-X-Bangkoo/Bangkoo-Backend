package com.bangkoo.back.service;

import com.bangkoo.back.model.user.User;
import com.bangkoo.back.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // ✅ OAuth2User 가져오기

        // 카카오 로그인인지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"kakao".equals(registrationId)) {
            throw new OAuth2AuthenticationException("지원하지 않는 로그인입니다.");
        }

        // 사용자 정보 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttribute("kakao_account");
        if (kakaoAccount == null) {
            throw new OAuth2AuthenticationException("카카오 계정 정보를 가져올 수 없습니다.");
        }

        String email = (String) kakaoAccount.get("email");
        Map<String, Object> properties = (Map<String, Object>) oAuth2User.getAttribute("properties");
        String nickname = properties != null ? (String) properties.get("nickname") : "Unknown";

        System.out.println("✅ OAuth2 로그인 성공! 이메일: " + email + ", 닉네임: " + nickname);

        // DB에 사용자 저장
        saveUser(email, nickname);

        return oAuth2User;
    }

    private void saveUser(String email, String nickname) {
        Optional<User> existingUser = userRepository.findById(email); // ✅ ID 기반 조회

        if (existingUser.isEmpty()) {
            User user = new User(email, nickname);
            userRepository.save(user);
            System.out.println("🆕 사용자 저장 완료: " + email + " / " + nickname);
        } else {
            System.out.println("ℹ️ 이미 존재하는 사용자: " + email);
        }
    }
}
