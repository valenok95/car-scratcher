package ru.wallentos.carscratcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO фильтр для поиска авто.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiResponse(description = "DTO для получения авто по фильтру")
public class CarFilterRequestDto {
    private List<String> carIds;
    /**
     * Производитель.
     */
    private List<String> manufacturers;
    private List<String> models;
    private List<String> badges;
    private List<String> badgeDetails;
    private List<String> transmissions;
    private List<String> fuelTypes;
    private Integer yearMoreThan;
    private Integer yearLessThan;
    private Integer mileageMoreThan;
    private Integer mileageLessThan;
    private List<String> colors;
    private Integer priceMoreThan;
    private Integer priceLessThan;
    private Integer limit;
    private Integer skip;
    private List<String> officeCityStates;
}
