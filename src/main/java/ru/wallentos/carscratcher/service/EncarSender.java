package ru.wallentos.carscratcher.service;


import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClient;
import ru.wallentos.carscratcher.dto.EncarSearchResponseEntity;
import ru.wallentos.carscratcher.dto.VehicleResponse;
import ru.wallentos.carscratcher.exception.EmptyResponseException;

@Service
@Log4j2
public class EncarSender {
    @Value("${ru.wallentos.carscratcher.encar-car-header-info-method}")
    private String getCarHeaderInfoMethod;
    @Value("${ru.wallentos.carscratcher.encar-car-vehicle-method}")
    private String getVehicleInfoMethod;

    private RestClient restClient;

    @Autowired
    EncarSender(RestClient restClientEncar) {
        this.restClient = restClientEncar;
    }

    /**
     * Получить список автомобилей по лимиту
     */
    public EncarSearchResponseEntity getEncarInfoLimitedList(boolean count, int skip, int limit) {
        ResponseEntity<EncarSearchResponseEntity> response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("count", count)
                        .queryParam("q", "(And.Hidden.N._.CarType.A._.SellType.일반._.ServiceCopyCar.ORIGINAL._.(Or.FuelType.가솔린._.FuelType.디젤._.FuelType.LPG(일반인 구입_)._.FuelType.LPG._.FuelType.가솔린+전기._.FuelType.디젤+전기._.FuelType.LPG+전기._.FuelType.가솔린+LPG._.FuelType.가솔린+CNG._.FuelType.CNG.))")
                        // CarType.A -домашние и импортные
                        .queryParam("sr", String.format("|ModifiedDate|%d|%d", skip, limit))
                        .path(getCarHeaderInfoMethod).build())
                .header("User-Agent",
                        "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/117.0")
                .retrieve()
                .toEntity(EncarSearchResponseEntity.class);
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
        ResponseEntity<EncarSearchResponseEntity> response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("count", true)
                        .queryParam("q", "(And.Hidden.N._.CarType.Y.)")
                        .path(getCarHeaderInfoMethod).build())
                .retrieve()
                .toEntity(EncarSearchResponseEntity.class);
        if (ObjectUtils.isEmpty(response)) {
            throw new EmptyResponseException("Вернулся пустой ответ.", null);
        } else {
            return response.getBody().getCount();
        }
    }

    /**
     * Запрос объёма двигателя автомобиля из источника encar.com.
     *
     * @param carId
     * @return
     */
    public VehicleResponse.Spec getEncarDetailDataByEncarId(long carId) {
        VehicleResponse vehicleResponse = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(getVehicleInfoMethod + carId).build())
                .header("User-Agent",
                        "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/117.0")
                .retrieve()
                .body(VehicleResponse.class);
        
        if (Objects.isNull(vehicleResponse) || ObjectUtils.isEmpty(vehicleResponse.getSpec())) {
            throw new EmptyResponseException("Вернулся пустой ответ.", null);
        } else {
            log.debug("Получен объем двигателя {} для car id {} ",
                    vehicleResponse.getSpec().getVolume(),
                    carId);
            return vehicleResponse.getSpec();
        }
    }
}
