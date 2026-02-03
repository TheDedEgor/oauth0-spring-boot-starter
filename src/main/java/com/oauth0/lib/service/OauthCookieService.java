package com.oauth0.lib.service;

import jakarta.servlet.http.Cookie;

import java.time.Duration;
import java.time.ZonedDateTime;

public class OauthCookieService {
    public static Cookie createSession(String uuid, ZonedDateTime validUntil) {
        var cookie = new Cookie("OAUTH_SESSION_ID", uuid);
        var seconds = Duration.between(ZonedDateTime.now(), validUntil).getSeconds();
        cookie.setMaxAge((int) seconds);
        return cookie;
    }

    public static Cookie createToken(String token) {
        var cookie = new Cookie("OAUTH_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // ВАЖНО: для http://localhost установите false
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        return cookie;
    }
}
