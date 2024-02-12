package ru.wallentos.carscratcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Контейнер для информации из encar по тачкам.
 * Получаем с сайта.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EncarSearchResponseEntity {
    /**
     * Количество результатов.
     */
    @JsonProperty("Count")
    private int count;

    /**
     * Список автомобилей.
     */
    @JsonProperty("SearchResults")
    private List<CarEntity> searchResults;


    /**
     * Автомобиль с encar.com.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CarEntity {
        /**
         * Идентификатор автомобиля encar.
         */
        @JsonProperty("Id")
        private String carId;
        /**
         * Производитель.
         */
        @JsonProperty("Manufacturer")
        private String manufacturer;
        @JsonProperty("Model")
        private String model;
        @JsonProperty("Badge")
        private String badge;
        @JsonProperty("BadgeDetail")
        private String badgeDetail;
        @JsonProperty("Transmission")
        private String transmission;
        @JsonProperty("FuelType")
        private String fuelType;
        @JsonProperty("Year")
        private int year;
        @JsonProperty("Mileage")
        private int mileage;
        @JsonProperty("Color")
        private String color;
        @JsonProperty("Price")
        private int price;
        @JsonProperty("OfficeCityState")
        private String officeCityState;
        @JsonProperty("Photos")
        private List<Photo> photos;
    }

    /**
     * Фото авто с сайта encar
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Photo {
        private int ordering;
        private String location;
    }
}
