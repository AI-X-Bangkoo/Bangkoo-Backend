package com.bangkoo.back.controller.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class kakaoAuthController {
    /**
     * 카카오 로그인 후 리다이렉트되는 URI엔드포인트
     * 이 엔드포트에서 로그인 성공 후 사용자 정보를 리다이렉트 합니다.
     *
     * @param principal OAuth2 인증을 통해 제공되는 사용자 정보
     * @return 로그인 성공 메시지 또는 사용자 정보
     */
    @GetMapping("/login/oauth2/code/kakao")
    public String kakakoLoginSuccess(@AuthenticationPrincipal OAuth2User principal){
        //OAuth2User에서 카카오 사용자 정보 추출
        String username = principal.getAttribute("nickname");
        String useremail = principal.getAttribute("email");

        System.out.println("로그인 성공 :"+ username + ", "+ useremail);

        //로그인 후 리턴할 메시지와 페이지
        return "success";
    }

    /**
     * 로그인 화면으로 리다이렉트하거나 사용자가 처음 접근할 수 있는 엔드포인트
     */
    @GetMapping("/login")
    public String login(){
        //로그인 페이지로 이동하는 것으로 수정 예정
        return "로그인 페이지로 이동";
    }

    /**
     * 카카오 로그인 후 사용자 정보 출력 예시
     * /login/oauth2/code/kakao로 리다이렉트 될 때 사용자 정보를 받는 방식
     */

}
