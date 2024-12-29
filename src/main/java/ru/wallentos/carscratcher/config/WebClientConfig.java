package ru.wallentos.carscratcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Конфигурация Spring, содержащая настройку webClient для работы с API
 */
@Configuration
public class WebClientConfig {
    @Value("${ru.wallentos.carscratcher.encar-host}")
    private String encatHost;

    /**
     * WebClient bean настроенный данными из application.yaml для работы с jira API
     */
    @Bean
    public WebClient webClientEncar() {
        return WebClient.builder()
                .baseUrl(encatHost)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(700 * 1024)).build())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/117.0")
                .build();
    }
}
