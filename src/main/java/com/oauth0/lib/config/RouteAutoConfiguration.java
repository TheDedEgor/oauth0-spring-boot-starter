package com.oauth0.lib.config;

import com.oauth0.lib.apiClient.ApiClient;
import com.oauth0.lib.controller.RouteController;
import com.oauth0.lib.service.AuthorizationEventPublisher;
import com.oauth0.lib.service.OauthService;
import com.oauth0.lib.service.OauthSessionService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@AutoConfiguration
@AutoConfigureAfter({OauthAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class RouteAutoConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, OauthProperties properties) {
        var uriBuilderFactory = new DefaultUriBuilderFactory(properties.getOauthBaseUrl());
        return builder
            .uriTemplateHandler(uriBuilderFactory)
            .build();
    }

    @Bean
    public ApiClient apiClient(RestTemplate restTemplate) {
        return new ApiClient(restTemplate);
    }

    @Bean
    public AuthorizationEventPublisher authorizationEventPublisher(OauthService oauthService) {
        return new AuthorizationEventPublisher(oauthService);
    }

    @Bean
    public RouteController routeController(AuthorizationEventPublisher publisher,
                                           ApiClient apiClient,
                                           OauthProperties properties,
                                           OauthSessionService oauthSessionService,
                                           OauthService oauthService) {
        return new RouteController(publisher, apiClient, properties, oauthSessionService, oauthService);
    }
}
