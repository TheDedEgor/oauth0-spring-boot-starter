package com.oauth0.lib.service;

import com.oauth0.lib.config.OauthProperties;
import com.oauth0.lib.dto.response.AuthLinkDTO;
import com.oauth0.lib.dto.response.AuthSessionDTO;
import com.oauth0.lib.dto.response.AuthSessionInfoDTO;
import com.oauth0.lib.enums.AuthLinkType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class OauthService {
    private final Map<String, ZonedDateTime> validUntilMap = new ConcurrentHashMap<>();

    private final OauthProperties oauthProperties;

    public AuthSessionDTO getAuthSessionInfo(AuthSessionInfoDTO sessionInfo) {
        var sessionId = sessionInfo.getUuid();
        var link1 = buildAuthBotLink(sessionId);
        var link2 = buildAuthWebAppLink(sessionId);
        return new AuthSessionDTO(List.of(link1, link2), sessionId, sessionInfo.getExpiredAt());
    }

    public void saveValidUntil(String uuid, ZonedDateTime validUntil) {
        if (validUntil == null) {
            return;
        }
        validUntilMap.put(uuid, validUntil);
    }

    public ZonedDateTime removeValidUntil(String uuid) {
        return validUntilMap.remove(uuid);
    }

    public ZonedDateTime getValidUntil(String uuid) {
        return validUntilMap.get(uuid);
    }

    public Long getSecondsBeforeExpiredTime(String uuid) {
        var validUntil = getValidUntil(uuid);
        return Duration.between(ZonedDateTime.now(), validUntil).getSeconds();
    }

    private AuthLinkDTO buildAuthBotLink(String sessionId) {
        var link = UriComponentsBuilder
            .fromUriString(oauthProperties.getTgBaseUrl())
            .queryParam("start", sessionId)
            .build()
            .toUriString();
        return new AuthLinkDTO(link, AuthLinkType.BOT);
    }

    private AuthLinkDTO buildAuthWebAppLink(String sessionId) {
        var link = UriComponentsBuilder
            .fromUriString(oauthProperties.getTgBaseUrl())
            .path("/web")
            .queryParam("startapp", sessionId)
            .build()
            .toUriString();
        return new AuthLinkDTO(link, AuthLinkType.WEB_APP);
    }
}
