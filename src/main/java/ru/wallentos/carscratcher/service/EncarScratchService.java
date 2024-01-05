package ru.wallentos.carscratcher.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.wallentos.carscratcher.dto.CarFilterRequestDto;
import ru.wallentos.carscratcher.dto.CarFilterResponseDto;
import ru.wallentos.carscratcher.dto.EncarSearchResponseDto;
import ru.wallentos.carscratcher.dto.EncarSearchResponseEntity;
import ru.wallentos.carscratcher.mapper.EncarResponseMapper;
import ru.wallentos.carscratcher.repository.EncarRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class EncarScratchService {
    private final EncarResponseMapper encarResponseMapper;
    private final EncarRepository encarRepository;
    private final EncarSender encarSender;

    /**
     * Обновить базу автомобилей.
     */
    public void refreshCache() {
        encarRepository.deleteAllCars();
        int carCount = encarSender.getEncarDataCount();
        saveEncarDataByCarCount(carCount);

    }

    /**
     * Сохранить автомобили циклами по 400 отталкиваясь от общего количества.
     * Примерно 4 минуты на 147623 записи.
     */
    private void saveEncarDataByCarCount(int carCount) {
        for (int i = 0; i < carCount; i = i + 400) {
            log.info("Обработано {} автомобилей из {}", i, carCount);
            var currentResult = encarSender.getEncarInfoLimitedList(false, i, 400);
            saveCarsFromResponse(currentResult);
        }
    }


    public void saveCarsFromResponse(EncarSearchResponseEntity response) {
        EncarSearchResponseDto responseDto = encarResponseMapper.toDto(response);
        List<EncarSearchResponseDto.CarDto> carDtoListToSave =
                responseDto.getSearchResults();
        log.info("начинаем вставку записей bulk");
        encarRepository.insertOrUpdateCars(carDtoListToSave);
        log.info("завершили вставку записей bulk");
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
}
