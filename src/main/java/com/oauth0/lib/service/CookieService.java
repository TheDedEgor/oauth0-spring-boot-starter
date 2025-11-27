package com.oauth0.lib.service;

import jakarta.servlet.http.Cookie;

public class CookieService {
    public static Cookie create(String uuid) {
        var cookie = new Cookie("OAUTH_SESSION_ID", uuid);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // ВАЖНО: для http://localhost установите false
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        return cookie;
    }
}
