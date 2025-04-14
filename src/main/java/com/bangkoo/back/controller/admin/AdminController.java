package com.bangkoo.back.controller.admin;

import com.bangkoo.back.model.auth.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    /**
     * 관리자페이지로 이동
     */
    @RequestMapping("/adminBoard")
    public ResponseEntity<?> adminBoard(@AuthenticationPrincipal User user){
        if(!"admin".equalsIgnoreCase(user.getRole())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }
        return ResponseEntity.ok("관리자 대시보드입니다.");
    }
}
