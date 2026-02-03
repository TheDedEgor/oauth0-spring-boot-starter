package com.oauth0.lib.service;

import org.springframework.http.ResponseCookie;

public class OauthCookieService {
    public static ResponseCookie createToken(String token) {
        return ResponseCookie.from("OAUTH_TOKEN", token)
            .httpOnly(false)
            .secure(false)
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .build();
    }
}
