package com.oauth0.lib.config;

import com.oauth0.lib.service.OauthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Log4j2
@AutoConfiguration
@ConditionalOnClass(OauthService.class) // Активировать эту конфигурацию, только если OauthService в classpath
@EnableConfigurationProperties(OauthProperties.class) // Включить и зарегистрировать OauthProperties
public class OauthAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean // Создать бин, только если пользователь не определил свой собственный OauthService
    public OauthService oauthService(OauthProperties properties) {
        return new OauthService(properties);
    }
}