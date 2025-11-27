package com.oauth0.lib.dto.request;

import com.oauth0.lib.config.OauthProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
@NoArgsConstructor
public class CreateAuthSessionDTO {
    private String authUrl;
    private String serviceName;
    private String description;
    private String logoUrl;
    private Boolean permanent = false;
    private Long lifetimeSeconds = 300L;

    public CreateAuthSessionDTO(OauthProperties oauthProperties, AuthSessionTimeDTO authSessionTimeDTO) {
        this.authUrl = buildAuthUrl(oauthProperties);
        this.serviceName = oauthProperties.getServiceName();
        this.description = oauthProperties.getDescription();
        this.logoUrl = oauthProperties.getLogoUrl();
        this.lifetimeSeconds = authSessionTimeDTO.getLifetimeSeconds();
    }

    private String buildAuthUrl(OauthProperties oauthProperties) {
        return UriComponentsBuilder
            .fromUriString(oauthProperties.getServiceBaseUrl())
            .path(oauthProperties.getAuthEndpoint())
            .build()
            .toUriString();
    }
}
