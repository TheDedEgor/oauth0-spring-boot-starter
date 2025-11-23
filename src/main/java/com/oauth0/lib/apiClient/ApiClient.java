package com.oauth0.lib.apiClient;

import com.oauth0.lib.dto.request.CreateAuthSessionDTO;
import com.oauth0.lib.dto.response.AuthSessionInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class ApiClient {

    private final RestTemplate restTemplate;

    public AuthSessionInfoDTO create(CreateAuthSessionDTO createAuthSessionDTO) {
        var requestEntity = new HttpEntity<>(createAuthSessionDTO, null);
        return restTemplate.exchange("/api/session", HttpMethod.POST, requestEntity, AuthSessionInfoDTO.class).getBody();
    }
}
