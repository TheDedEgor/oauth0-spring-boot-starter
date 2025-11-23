package com.oauth0.lib.controller;

import com.oauth0.lib.apiClient.ApiClient;
import com.oauth0.lib.config.OauthProperties;
import com.oauth0.lib.dto.request.CreateAuthSessionDTO;
import com.oauth0.lib.dto.request.CreateAuthSessionTimeDTO;
import com.oauth0.lib.dto.response.AuthSessionDTO;
import com.oauth0.lib.dto.response.UserDTO;
import com.oauth0.lib.service.AuthorizationEventPublisher;
import com.oauth0.lib.service.OauthService;
import com.oauth0.lib.service.OauthSessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class RouteController {

    private final AuthorizationEventPublisher publisher;

    private final ApiClient apiClient;

    private final OauthProperties properties;

    private final OauthSessionService oauthSessionService;

    private final OauthService oauthService;

    @GetMapping("${oauth0.auth-event-endpoint:/api/oauth0/auth-events}")
    public SseEmitter authEvent(@CookieValue(name = "OAUTH_SESSION_ID") String uuid) {
        return publisher.subscribe(uuid);
    }

    @PostMapping("${oauth0.create-endpoint:/api/oauth0/create}")
    public AuthSessionDTO createSession(@RequestBody(required = false) CreateAuthSessionTimeDTO authSessionTime,
                                        HttpServletResponse response) {
        var sessionInfoDTO = apiClient.create(new CreateAuthSessionDTO(properties, authSessionTime));
        var authSession = oauthService.getAuthSessionInfo(sessionInfoDTO);

        var cookie = new Cookie("OAUTH_SESSION_ID", sessionInfoDTO.getUuid());
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // ВАЖНО: для http://localhost установите false
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        return authSession;
    }

    @PostMapping("${oauth0.auth-endpoint:/api/oauth0/auth}")
    public void auth(@RequestBody UserDTO user) {
        oauthSessionService.create(user.getUuid(), user.getId());
        publisher.publish(user.getUuid());
    }
}
