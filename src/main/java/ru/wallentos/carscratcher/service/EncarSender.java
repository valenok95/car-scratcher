package ru.wallentos.carscratcher.service;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;
import ru.wallentos.carscratcher.dto.EncarSearchResponseEntity;
import ru.wallentos.carscratcher.exception.EmptyResponseException;

@Service
@Log4j2
public class EncarSender {
    @Value("${ru.wallentos.carscratcher.encar-car-header-info-method}")
    private String getCarHeaderInfoMethod;

    private WebClient webClient;

    @Autowired
    EncarSender(WebClient webClientEncar) {
        this.webClient = webClientEncar;
    }

    /**
     * Получить список автомобилей по лимиту
     */
    public EncarSearchResponseEntity getEncarInfoLimitedList(boolean count, int skip, int limit) {
        ResponseEntity<EncarSearchResponseEntity> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("count", count)
                        .queryParam("q", "(And.Hidden.N._.CarType.A._.Condition.Record.)")
                        .queryParam("sr", String.format("|ModifiedDate|%d|%d", skip, limit))
                        .path(getCarHeaderInfoMethod).build())
                .retrieve()
                .toEntity(EncarSearchResponseEntity.class)
                .block();
        if (ObjectUtils.isEmpty(response)) {
            throw new EmptyResponseException("Вернулся пустой ответ.", null);
        } else {
            return response.getBody();
        }
    }

    /**
     * Получить количество автомобилей.
     */
    public int getEncarDataCount() {
        ResponseEntity<EncarSearchResponseEntity> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("count", true)
                        .queryParam("q", "(And.Hidden.N._.CarType.A._.Condition.Record.)")
                        .path(getCarHeaderInfoMethod).build())
                .retrieve()
                .toEntity(EncarSearchResponseEntity.class)
                .block();
        if (ObjectUtils.isEmpty(response)) {
            throw new EmptyResponseException("Вернулся пустой ответ.", null);
        } else {
            return response.getBody().getCount();
        }
    }
}
