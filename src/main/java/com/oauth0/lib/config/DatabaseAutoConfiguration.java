package com.oauth0.lib.config;

import com.oauth0.lib.repository.OauthSessionRepository;
import com.oauth0.lib.service.OauthSessionService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@AutoConfiguration
@AutoConfigurationPackage(basePackages = {"com.oauth0.lib.model", "com.oauth0.lib.repository"})
@ConditionalOnClass({DataSource.class}) // Активируем, если есть JPA
@ConditionalOnBean(DataSource.class)    // Активируем, если есть настроенный DataSource
@EnableTransactionManagement
public class DatabaseAutoConfiguration {

    @Bean
    public OauthSessionService sessionService(OauthSessionRepository oauthSessionRepository) {
        return new OauthSessionService(oauthSessionRepository);
    }
}
