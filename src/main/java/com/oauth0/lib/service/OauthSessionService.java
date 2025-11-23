package com.oauth0.lib.service;

import com.oauth0.lib.model.OauthSession;
import com.oauth0.lib.repository.OauthSessionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OauthSessionService {

    private final OauthSessionRepository oauthSessionRepository;

    public void create(String sessionId, Long providerId) {
        var session = new OauthSession(sessionId, providerId);
        oauthSessionRepository.save(session);
    }

    public Long auth(String sessionId) {
        var session = oauthSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        return session.getProviderId();
    }

    public void logout(String sessionId) {
        oauthSessionRepository.deleteById(sessionId);
    }
}
