package com.bangkoo.back.controller.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class KakaoAuthController {
    /**
     * 카카오 로그인 후 리다이렉트되는 URI엔드포인트
     * 이 엔드포트에서 로그인 성공 후 사용자 정보를 리다이렉트 합니다.
     *
     * @param principal OAuth2 인증을 통해 제공되는 사용자 정보
     * @return 로그인 성공 메시지 또는 사용자 정보
     */
    @GetMapping("/oauth/login/code/kakao")
    public ResponseEntity<String> kakaoLoginSuccess(@AuthenticationPrincipal OAuth2User principal){

        if (principal == null) {
            return ResponseEntity.status(401).body("로그인되지 않은 사용자입니다.");
        }

        //OAuth2User에서 카카오 사용자 정보 추출
        String username = principal.getAttribute("nickname");
        String useremail = principal.getAttribute("email");

        System.out.println("로그인 성공 :"+ username + ", 이메일:"+ useremail);

        //로그인 후 리턴할 메시지와 페이지
        return ResponseEntity.ok("로그인 성공 :" +username + "(" + useremail + ")");
    }

    /**
     * 로그인 화면으로 리다이렉트하거나 사용자가 처음 접근할 수 있는 엔드포인트
     */
    @GetMapping("/login")
    public String login(){
        //로그인 페이지로 이동하는 것으로 수정 예정`
        return "로그인 페이지로 이동";
    }

    /**
     * 카카오 로그인 후 사용자 정보 출력 예시
     * /login/oauth2/code/kakao로 리다이렉트 될 때 사용자 정보를 받는 방식
     */
    @GetMapping("/user")
    public String getUserInfo(@AuthenticationPrincipal OAuth2User user){
        if(user != null){
            String name = user.getAttribute("nickname");
            String email = user.getAttribute("email");
            return "사용자 정보:" + name + ", 이메일:" + email;
        }
        return "로그인된 사용자가 없습니다.";
    }

    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<String> kakaoCallback(@RequestParam("code") String code) {
        System.out.println("인가 코드: {}" + code);
        return ResponseEntity.ok("인가 코드: " + code);
    }
}
