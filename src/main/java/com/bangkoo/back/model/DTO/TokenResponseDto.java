package com.bangkoo.back.model.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponseDto {

    private String accessToken;
    private String refreshToken;
    private String email;
    private String nickname;
}
