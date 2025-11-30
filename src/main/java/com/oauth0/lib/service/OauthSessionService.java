package com.oauth0.lib.service;

import com.oauth0.lib.model.OauthSession;
import com.oauth0.lib.repository.OauthSessionRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class OauthSessionService {

    private final OauthSessionRepository oauthSessionRepository;

    public void create(String sessionId, Long providerId) {
        var session = new OauthSession(sessionId, providerId);
        oauthSessionRepository.save(session);
    }

    public Optional<Long> auth(String sessionId) {
        var optionalSession = oauthSessionRepository.findById(sessionId);
        return optionalSession.map(OauthSession::getProviderId);
    }

    public void logout(String sessionId) {
        oauthSessionRepository.deleteById(sessionId);
    }
}
