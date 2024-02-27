package ru.wallentos.carscratcher.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.wallentos.carscratcher.dto.CarFilterRequestDto;
import ru.wallentos.carscratcher.dto.CarFilterResponseDto;
import ru.wallentos.carscratcher.dto.EncarSearchResponseDto;
import ru.wallentos.carscratcher.dto.EncarSearchResponseEntity;
import ru.wallentos.carscratcher.mapper.EncarResponseMapperImpl;
import ru.wallentos.carscratcher.repository.EncarRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class EncarScratchService {
    private final EncarResponseMapperImpl encarResponseMapper;
    private final EncarRepository encarRepository;
    private final EncarSender encarSender;

    /**
     * Обновить базу автомобилей.
     */
    public void refreshCache() {
        log.info("Проверяем актуальное количество объявлений");
        int carCount = encarSender.getEncarDataCount();
        log.info("В новый КЭШ будет добавлено {} объявлений", carCount);
        log.info("удаляем текущий КЭШ");
        encarRepository.deleteAllCars();
        saveEncarDataByCarCount(carCount);

    }

    /**
     * Сохранить автомобили циклами по 400 отталкиваясь от общего количества.
     * Примерно 4 минуты на 147623 записи.
     */
    private void saveEncarDataByCarCount(int carCount) {
        log.info("начинаем добавлять объявления в КЭШ");
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
     * Поиск авто в БД по id.
     *
     * @param carId
     * @return carResponse
     */
    public EncarSearchResponseDto.CarDto findCarsById(int carId) {
        log.info("Поиск авто в БД Mongo по id: {}", carId);
        return encarRepository.findCarsById(carId);
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
