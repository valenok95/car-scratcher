package ru.wallentos.carscratcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Сущность для работы с БД.
 */
@Data
public class EncarDto {
    /**
     * Количество результатов.
     */
    private int count;

    /**
     * Список автомобилей.
     */
    private List<CarDto> searchResults;


    /**
     * Автомобиль с encar.com.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Document("car")
    public static class CarDto {
        /**
         * Идентификатор автомобиля encar.
         */
        @Id
        private long carId;
        /**
         * Производитель.
         */
        private String manufacturer;
        private String model;
        private String badge;
        private String badgeDetail;
        private String transmission;
        private String fuelType;
        private int yearMonth;
        private int mileage;
        private int volume;
        private String color;
        /**
         * Цена в валюте, полученная с сайта.
         */
        private int originalPrice;
        /**
         * Финальная цена в рублях.
         */
        private int finalPriceInRubles;
        private String officeCityState;
        private WDType wdType;
        private List<Photo> photos;
        private Detalization detalization;

        @Version
        private int version;
        /**
         * Дата и время обновления записи.
         */
        @CreatedDate
        private Instant createDate;
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

    /**
     * Детализация после калькуляции.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Detalization {
        private CarCategory carCategory;
        private double feeRate;
        private double duty;
        private int firstPriceInRubles;
        private double recyclingFee;
        private double extraPayAmountValutePart;
        private int extraPayAmountRublePart;
        private String location;
        private String originalLink;
    }
}
