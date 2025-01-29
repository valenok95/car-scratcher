package ru.wallentos.carscratcher.service;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.wallentos.carscratcher.dto.CalculatorRequestDto;
import ru.wallentos.carscratcher.dto.CalculatorResponseDto;
import ru.wallentos.carscratcher.dto.CarFilterRequestDto;
import ru.wallentos.carscratcher.dto.CarFilterResponseDto;
import ru.wallentos.carscratcher.dto.EncarDto;
import ru.wallentos.carscratcher.dto.EncarSearchResponseEntity;
import ru.wallentos.carscratcher.dto.VehicleResponse;
import ru.wallentos.carscratcher.mapper.EncarResponseMapper;
import ru.wallentos.carscratcher.repository.EncarRepository;


@Log4j2
@Service
@RequiredArgsConstructor
public class EncarScratchService {
    private final EncarResponseMapper encarResponseMapper;
    private final EncarRepository encarRepository;
    private final EncarSender encarSender;
    private final CalculatorService calculatorService;

    @Value("${ru.wallentos.carscratcher.encar-original-link}")
    private String encarOriginalLink;

    /**
     * Обновить базу автомобилей.
     */
    public void refreshCache() {
        log.info("Проверяем актуальное количество объявлений");
        int carCount = encarSender.getEncarDataCount();
        log.info("В новый КЭШ будет добавлено {} объявлений", carCount);
        fillDbWithFullEncarDataByCarCount(carCount);
        
/*        log.info("удаляем текущий КЭШ который не обновился за сутки");
;*/
        removeOldCars(); // доделать

    }

    /**
     * Удаляем автомобили старше суток.
     */
    private void removeOldCars() {
        // удаляем автомобили старше суток.
    }
//TODO Прерывание одного из запросов не должно ломать всё обогащение.

    /**
     * Сохранить автомобили циклами по 400 отталкиваясь от общего количества.
     * Примерно 4 минуты на 147623 записи.
     */
    private void fillDbWithFullEncarDataByCarCount(int carCount) {
        log.info("начинаем добавлять объявления в КЭШ");
        for (int i = 0; i < carCount; i = i + 400) {
            log.info("Обработано {} автомобилей из {}", i, carCount);
            try {
                EncarSearchResponseEntity currentResult = encarSender.getEncarInfoLimitedList(false, i, 400);
                //TODO обогащать на ходу по 400.
                EncarDto responseDto = encarResponseMapper.toDto(currentResult);
                // обогащаем объемом двигателя все полученные автомобили для расчётов.
                List<EncarDto.CarDto> carDtoListToSave =
                        getEnrichedCarVolumeForList(responseDto.getSearchResults());
                enrichDetalizationForCars(carDtoListToSave);

                encarRepository.insertOrReplaceCars(carDtoListToSave);
            } catch (Exception e) {
                log.error("Ошибка при обработке запроса в encar! {}", e.getCause().getMessage());
            }
        }
    }

    /**
     * Обогатить объем двигателя автомобилю для расчёта стоимости.
     * Либо из источника encar, либо из базы.
     *
     * @param carDtoList исходный список автомобилей.
     * @return список автомобилей с объемом двигателя.
     */
    private List<EncarDto.CarDto> getEnrichedCarVolumeForList(List<EncarDto.CarDto> carDtoList) {
        Map<Long, EncarDto.CarDto> newCarIdToCarDtoMap =
                carDtoList.stream().collect(toMap(EncarDto.CarDto::getCarId, carDto -> carDto));
        log.info("Выполняем поиск автомобилей в базе по id");
        List<EncarDto.CarDto> carsFromDbList = encarRepository.getCarsByIds(newCarIdToCarDtoMap.keySet());
        log.info("Обнаружено {} автомобилей с объемом двигателя, обновим им цену.", carsFromDbList.size());
        carsFromDbList.forEach(encarDtoFromDb -> {
            int newPrice = newCarIdToCarDtoMap.get(encarDtoFromDb.getCarId()).getOriginalPrice();
            encarDtoFromDb.setOriginalPrice(newPrice);
            newCarIdToCarDtoMap.remove(encarDtoFromDb.getCarId());
        });

        log.info("Новых автомобилей: {}, объем двигателя получим из encar.com.",
                newCarIdToCarDtoMap.size());
        List<EncarDto.CarDto> resultCarList =
                new java.util.ArrayList<>(newCarIdToCarDtoMap.values().stream().map(car -> {
                    VehicleResponse.Spec detailEnarInfo =
                            encarSender.getEncarDetailDataByEncarId(car.getCarId());
                    car.setBodyName(detailEnarInfo.getBodyName());
                    car.setVolume(detailEnarInfo.getVolume());
                    return car;
                }).toList());
        resultCarList.addAll(carsFromDbList);
        return resultCarList;
    }

    /**
     * Расчёт детализации по данным автомобиля. (дополняем поля)
     *
     * @param carDto данные по автомобилю для расчёта всех расходов.
     */
    private void enrichDetalizationForCar(EncarDto.CarDto carDto) {
        log.debug("Начинаем калькулировать информацию о расчёте автомобиля {}", carDto.getCarId());
        CalculatorResponseDto calculationResult = calculatorService.calculateKoreaCarPrice(CalculatorRequestDto.builder()
                .originalPrice(carDto.getOriginalPrice())
                .yearMonth(carDto.getYearMonth())
                .volume(carDto.getVolume()).build());

        carDto.setDetalization(EncarDto.Detalization.builder()
                .recyclingFee(calculationResult.getRecyclingFee())
                .carCategory(calculationResult.getCarCategory())
                .duty(calculationResult.getDuty())
                .extraPayAmountRublePart(calculationResult.getExtraPayAmountRublePart())
                .extraPayAmountValutePart(calculationResult.getExtraPayAmountValutePart())
                .feeRate(calculationResult.getFeeRate())
                .firstPriceInRubles(calculationResult.getFirstPriceInRubles())
                .location(calculationResult.getLocation())
                .originalLink(encarOriginalLink + carDto.getCarId()).build());
        carDto.setFinalPriceInRubles(calculationResult.getResultPriceInRubles());
        log.debug("Закончили калькулировать информацию о расчёте автомобиля {}", carDto.getCarId());
    }

    /**
     * Расчёт детализации по данным автомобилей. (дополняем поля)
     *
     * @param carDtoList данные по автомобилю для расчёта всех расходов.
     */
    private void enrichDetalizationForCars(List<EncarDto.CarDto> carDtoList) {
        carDtoList.forEach(this::enrichDetalizationForCar);
    }

    /**
     * Поиск авто в БД по фильтру CarFilterRequestDto.
     *
     * @param filter
     * @return
     */
    public CarFilterResponseDto findCarsByFilter(CarFilterRequestDto filter) {
        log.info("Поиск авто в БД Mongo по фильтру: {}", filter);
        return encarRepository.findCarsByFilter(filter);
    }

    /**
     * Получить список моделей по марке авто.
     *
     * @param markName производитель
     * @return список моделей
     */
    public List<String> getModelListByMarkName(String markName) {
        log.info("Получаем список моделей авто в БД Mongo для марки {}", markName);
        return encarRepository.getModelListByMarkName(markName);
    }

    /**
     * Получить список марок.
     *
     * @return список марок
     */
    public List<String> getMarkList() {
        log.info("Получаем список марок авто");
        return encarRepository.getMarkList();
    }
}

