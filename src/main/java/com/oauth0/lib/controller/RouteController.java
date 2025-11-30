package com.oauth0.lib.controller;

import com.oauth0.lib.apiClient.ApiClient;
import com.oauth0.lib.config.OAuthUserDataProcessor;
import com.oauth0.lib.config.OauthProperties;
import com.oauth0.lib.dto.request.AuthSessionTimeDTO;
import com.oauth0.lib.dto.request.CreateAuthSessionDTO;
import com.oauth0.lib.dto.response.AuthSessionDTO;
import com.oauth0.lib.dto.response.UserDTO;
import com.oauth0.lib.service.AuthorizationEventPublisher;
import com.oauth0.lib.service.OauthCookieService;
import com.oauth0.lib.service.OauthService;
import com.oauth0.lib.service.OauthSessionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequiredArgsConstructor
public class RouteController {

    private final AuthorizationEventPublisher publisher;

    private final ApiClient apiClient;

    private final OauthProperties properties;

    private final OauthSessionService oauthSessionService;

    private final OauthService oauthService;

    private final OAuthUserDataProcessor  userDataProcessor;

    @GetMapping("${oauth0.auth-event-endpoint:/api/oauth0/auth-events}")
    public SseEmitter authEvent(@CookieValue(name = "OAUTH_SESSION_ID") String uuid) {
        return publisher.subscribe(uuid);
    }

    @PostMapping("${oauth0.create-endpoint:/api/oauth0/create}")
    public AuthSessionDTO createSession(@RequestBody(required = false) AuthSessionTimeDTO authSessionTime,
                                        HttpServletResponse response) {
        var sessionInfoDTO = apiClient.create(new CreateAuthSessionDTO(properties, authSessionTime));
        var authSession = oauthService.getAuthSessionInfo(sessionInfoDTO);
        // Сохраняем время до скольки действительна сессия
        oauthService.saveValidUntil(authSession.getSessionId(), authSession.getValidUntil());

        var cookie = OauthCookieService.create(sessionInfoDTO.getUuid());
        response.addCookie(cookie);

        return authSession;
    }

    @PostMapping("${oauth0.auth-endpoint:/api/oauth0/auth}")
    public void auth(@RequestBody UserDTO user) throws ExecutionException, InterruptedException, TimeoutException {
        oauthSessionService.create(user.getUuid(), user.getId());
        userDataProcessor.save(user);
        var sseCompletionFuture = publisher.publish(user.getUuid());
        var timeout = oauthService.getSecondsBeforeExpiredTime(user.getUuid());
        sseCompletionFuture.get(timeout, TimeUnit.SECONDS);
    }

    @PostMapping("${oauth0.auth-confirm-endpoint:/api/oauth0/auth-confirm}")
    public void authConfirm(@CookieValue(name = "OAUTH_SESSION_ID") String uuid) {
        publisher.complete(uuid);
    }
}
