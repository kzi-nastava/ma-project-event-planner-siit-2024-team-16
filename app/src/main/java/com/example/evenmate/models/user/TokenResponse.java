package com.example.evenmate.models.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenResponse {
    private String accessToken;
    private int expiresIn;

    public TokenResponse() {
    }

    public TokenResponse(String accessToken, int expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}
