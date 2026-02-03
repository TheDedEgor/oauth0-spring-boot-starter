package com.oauth0.lib.controller;

import com.oauth0.lib.apiClient.ApiClient;
import com.oauth0.lib.config.OAuthUserDataProcessor;
import com.oauth0.lib.config.OauthProperties;
import com.oauth0.lib.dto.request.AuthSessionTimeDTO;
import com.oauth0.lib.dto.request.CreateAuthSessionDTO;
import com.oauth0.lib.dto.response.AuthSessionDTO;
import com.oauth0.lib.dto.response.ErrorNotificationDto;
import com.oauth0.lib.dto.response.UserDTO;
import com.oauth0.lib.service.OauthWaitRegistry;
import com.oauth0.lib.service.OauthService;
import com.oauth0.lib.service.OauthSessionService;
import com.oauth0.lib.service.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.ZonedDateTime;

@RestController
@RequiredArgsConstructor
public class RouteController {

    private final ApiClient apiClient;

    private final OauthProperties properties;

    private final OauthSessionService oauthSessionService;

    private final OauthService oauthService;

    private final OAuthUserDataProcessor  userDataProcessor;

    private final OauthWaitRegistry oauthWaitRegistry;

    @PostMapping("${oauth0.create-endpoint:/api/oauth0/create}")
    public AuthSessionDTO createSession(@RequestBody(required = false) AuthSessionTimeDTO authSessionTime) {
        var sessionInfoDTO = apiClient.create(new CreateAuthSessionDTO(properties, authSessionTime));
        var authSession = oauthService.getAuthSessionInfo(sessionInfoDTO);
        oauthWaitRegistry.addSession(authSession);
        return authSession;
    }

    @PostMapping("${oauth0.auth-callback-endpoint:/api/oauth0/callback}")
    public void auth(@RequestBody UserDTO user) {
        userDataProcessor.save(user);
        var token = TokenGenerator.generate(64);
        oauthSessionService.create(token, user.getId());
        oauthWaitRegistry.complete(user.getUuid(), token);
    }

    @PostMapping("${oauth0.auth-endpoint:/api/oauth0/auth}")
    public DeferredResult<ResponseEntity<?>> auth(@RequestParam String sessionId) {
        var session = oauthWaitRegistry.getSession(sessionId);

        // Сессии не существует
        if (session == null) {
            var result = new DeferredResult<ResponseEntity<?>>();
            result.setResult(ResponseEntity.notFound().build());
            return result;
        }
        // Сессия истекла
        if (session.getValidUntil().isBefore(ZonedDateTime.now())) {
            oauthWaitRegistry.remove(sessionId);

            var result = new DeferredResult<ResponseEntity<?>>();
            result.setResult(ResponseEntity.status(410).build());
            return result;
        }

        var result = new DeferredResult<ResponseEntity<?>>(25_000L);

        oauthWaitRegistry.add(sessionId, result);

        result.onTimeout(() -> {
            oauthWaitRegistry.remove(sessionId);
            result.setResult(ResponseEntity.noContent().build());
        });

        result.onCompletion(() -> oauthWaitRegistry.remove(sessionId));

        return result;
    }

    @PostMapping("${oauth0.auth-error-callback-endpoint:/api/oauth0/error}")
    public void error(@RequestBody ErrorNotificationDto errorNotificationDto) {
        oauthWaitRegistry.error(errorNotificationDto);
    }
}
