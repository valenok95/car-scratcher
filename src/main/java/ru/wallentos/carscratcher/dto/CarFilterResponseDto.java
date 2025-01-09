package ru.wallentos.carscratcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiResponse(description = "DTO для получения авто по фильтру")
public class CarFilterResponseDto {

    private long count;
    /**
     * Список автомобилей.
     */
    private List<EncarDto.CarDto> searchResults;

}
