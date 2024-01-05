package ru.wallentos.carscratcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.Date;
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
public class EncarSearchResponseDto {
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
        private String carId;
        /**
         * Производитель.
         */
        private String manufacturer;
        private String model;
        private String badge;
        private String badgeDetail;
        private String transmission;
        private String fuelType;
        private int year;
        private int mileage;
        private String color;
        private int price;
        private String officeCityState;
        private List<EncarSearchResponseEntity.Photo> photos;

        @Version
        private int version;
        /**
         * Дата и время создания записи.
         */
        @CreatedDate
        private Instant createdDate;
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
