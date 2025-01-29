package ru.wallentos.carscratcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Контейнер для информации из encar по конкретному автомобилю.
 * Получаем с сайта.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleResponse {
    /**
     * Количество результатов.
     */
    @JsonProperty("spec")
    private Spec spec;

    /**
     * Автомобиль с encar.com.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Spec {
        /**
         * Объем двигателя.
         */
        @JsonProperty("displacement")
        private int volume;
        /**
         * Производитель.
         */
        @JsonProperty("bodyName")
        private String bodyName;
    }}