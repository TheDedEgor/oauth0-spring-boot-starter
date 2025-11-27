package com.oauth0.lib.config;

import com.oauth0.lib.repository.OauthSessionRepository;
import com.oauth0.lib.service.OauthSessionService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@AutoConfiguration
@AutoConfigurationPackage(basePackages = {"com.oauth0.lib.model", "com.oauth0.lib.repository"})
@ConditionalOnClass(DataSource.class) // Активируем, если есть JPA
@EnableTransactionManagement
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class OauthDatabaseAutoConfiguration {

    @Bean
    public OauthSessionService oauthSessionService(OauthSessionRepository oauthSessionRepository) {
        return new OauthSessionService(oauthSessionRepository);
    }
}
