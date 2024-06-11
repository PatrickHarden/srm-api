package com.scholastic.srmapi.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BaWebClientConfig {
    private static final String AUTHORIZATION = "authorization";

    @Value("${kongToken}")
    @Getter
    private String kongToken;

    @Value("${baServiceBaseUrl}")
    @Getter
    private String baServiceBaseUrl;

    @Bean
    public WebClient baWebClient() {
        return WebClient.builder()
                .defaultHeader(AUTHORIZATION, "Bearer " + kongToken)
                .baseUrl(baServiceBaseUrl)
                .build();
    }
}
