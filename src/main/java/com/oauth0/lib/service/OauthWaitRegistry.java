package com.oauth0.lib.service;

import com.oauth0.lib.dto.response.AuthSessionDTO;
import com.oauth0.lib.dto.response.ErrorNotificationDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OauthWaitRegistry {
    // TODO переделать на Redis?
    private final Map<String, AuthSessionDTO> sessions = new ConcurrentHashMap<>();
    private final Map<String, DeferredResult<ResponseEntity<?>>> waiters = new ConcurrentHashMap<>();

    public void addSession(AuthSessionDTO authSessionDTO) {
        sessions.put(authSessionDTO.getSessionId(), authSessionDTO);
    }

    public AuthSessionDTO getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void add(String sessionId, DeferredResult<ResponseEntity<?>> result) {
        waiters.put(sessionId, result);
    }

    public void complete(String sessionId, String token) {
        var result = waiters.remove(sessionId);
        if (result == null) {
            throw new IllegalStateException("No deferred result found for session id " + sessionId);
        }
        var cookie = OauthCookieService.createToken(token);
        var response = ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build();
        result.setResult(response);
    }

    public void error(ErrorNotificationDto errorNotificationDto) {
        var sessionId = errorNotificationDto.getSessionId();
        sessions.remove(sessionId);
        var result = waiters.remove(sessionId);
        if (result == null) {
            throw new IllegalStateException("No deferred result found for session id " + sessionId);
        }
        result.setErrorResult(errorNotificationDto);
    }

    public void remove(String sessionId) {
        waiters.remove(sessionId);
        sessions.remove(sessionId);
    }
}

