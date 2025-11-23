package com.oauth0.lib.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = "oauth0")
@Validated
public class OauthProperties {
    @URL(message = "Должен быть корректный URL")
    @NotBlank(message = "URL обязательно должен быть указан")
    private String oauthBaseUrl = "https://oauth0.ru";
    @URL(message = "Должен быть корректный URL")
    @NotBlank(message = "URL обязательно должен быть указан")
    private String serviceBaseUrl;
    @URL(message = "Должен быть корректный URL")
    private String tgBaseUrl = "https://t.me/OAuthZeroBot";
    @NotBlank(message = "URL обязательно должен быть указан")
    private String createEndpoint = "/api/oauth0/create";
    @NotBlank(message = "URL обязательно должен быть указан")
    private String authEndpoint = "/api/oauth0/auth";
    @NotBlank(message = "URL обязательно должен быть указан")
    private String authEventEndpoint = "/api/oauth0/auth-events";
    @NotBlank(message = "Название сервиса обязательно")
    private String serviceName = "Название сервиса";
    private String description;
    private String logoUrl;
}