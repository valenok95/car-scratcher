package ru.wallentos.carscratcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Конфигурация Spring, содержащая настройку webClient для работы с API
 */
@Configuration
public class RestClientConfig {
    @Value("${ru.wallentos.carscratcher.encar-host}")
    private String encatHost;

    /**
     * RestClient bean настроенный данными из application.yaml для работы с encar API
     */
    @Bean
    public RestClient restClientEncar() {
        return RestClient.builder()
                .baseUrl(encatHost)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/117.0")
                .build();
    }
}
