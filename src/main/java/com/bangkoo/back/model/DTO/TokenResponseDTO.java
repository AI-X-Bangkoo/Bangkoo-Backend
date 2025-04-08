package com.bangkoo.back.model.DTO;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponseDTO {

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
