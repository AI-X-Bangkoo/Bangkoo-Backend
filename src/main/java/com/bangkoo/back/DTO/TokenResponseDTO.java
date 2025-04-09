package com.bangkoo.back.DTO;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponseDTO {

    /**
     *카카오 로그인 후 클라이언트에게 반환되는
     * 정보들 중
     * 엑세스 토큰, 리프레쉬 토큰, 이메일, 닉네임, 로그인 유무
     */

    private String accessToken;
    private String refreshToken;
    private String email;
    private String nickname;
    private boolean login;



    @Override
    public String toString() {
        return "TokenResponseDTO{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", login=" + login +
                '}';
    }
}
