package com.oauth0.lib.apiClient;

import com.oauth0.lib.config.OauthProperties;
import com.oauth0.lib.dto.request.CreateAuthSessionDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ReactiveApiClient {

    private final WebClient webClient;

    public ReactiveApiClient(OauthProperties properties) {
        this.webClient = WebClient.builder()
            .baseUrl(properties.getOauthBaseUrl())
            .build();
    }

    public Mono<String> create(CreateAuthSessionDTO createAuthSessionDTO) {
        return webClient.post()
            .uri("/api/session/create")
            .bodyValue(createAuthSessionDTO)
            .retrieve()
            .bodyToMono(String.class);
    }
}
