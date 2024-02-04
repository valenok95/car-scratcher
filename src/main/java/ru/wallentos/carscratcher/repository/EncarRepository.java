package ru.wallentos.carscratcher.repository;

import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import ru.wallentos.carscratcher.dto.CarFilterRequestDto;
import ru.wallentos.carscratcher.dto.CarFilterResponseDto;
import ru.wallentos.carscratcher.dto.EncarSearchResponseDto;

@Repository
@Log4j2
@RequiredArgsConstructor
public class EncarRepository {
    private final String ENCAR_RESULT_COLLECTION_NAME = "car";
    private final String PRICE_FIELD_NAME = "price";
    private final String MILEAGE_FIELD_NAME = "mileage";
    private final String YEAR_FIELD_NAME = "year";
    private final MongoTemplate mongoTemplate;


    public void test() {
        mongoTemplate.findAll(Object.class, ENCAR_RESULT_COLLECTION_NAME);
    }

    /**
     * Вставка авто в БД.
     *
     * @param carDto
     */
    public void insertCar(EncarSearchResponseDto.CarDto carDto) {
        mongoTemplate.insert(carDto);
    }

    /**
     * Вставка авто в БД.
     *
     * @param carDtoList список авто для сохранения.
     */
    public void insertCars(List<EncarSearchResponseDto.CarDto> carDtoList) {
        mongoTemplate.insertAll(carDtoList);
    }

    /**
     * Чистка коллекции авто.
     */
    public void deleteAllCars() {
        mongoTemplate.dropCollection("car");
    }

    /**
     * Вставка/Обновление авто в БД.
     *
     * @param carDtoList список авто для сохранения.
     */
    public void insertOrUpdateCars(List<EncarSearchResponseDto.CarDto> carDtoList) {
        mongoTemplate.findAllAndRemove(
                new Query(
                        Criteria.where("_id")
                                .in(carDtoList.stream().map(EncarSearchResponseDto.CarDto::getCarId)
                                        .toList())),
                "car");
        mongoTemplate.insertAll(carDtoList);
    }

    /**
     * Поиск авто в БД по фильтру.
     *
     * @param filter фильтр
     * @return список тачек из БД по фильтру
     */
    public CarFilterResponseDto findCarsByFilter(CarFilterRequestDto filter) {
        //  var resultQuery2 = buildQueryForCarFindByFilterByOneCriteria(filter);
        var resultQuery = buildQueryForCarFindByFilterByLotsOfCriterias(filter);
        long count = mongoTemplate.count(resultQuery, "car");
        if (!ObjectUtils.isEmpty(filter.getLimit())) {
            resultQuery.limit(filter.getLimit());
        } else {
            resultQuery.limit(20);
        }
        if (!ObjectUtils.isEmpty(filter.getSkip())) {
            resultQuery.skip(filter.getSkip());
        }
        List<EncarSearchResponseDto.CarDto> searchResults =
                mongoTemplate.find(resultQuery, EncarSearchResponseDto.CarDto.class);

        log.info("выполняется запрос: {}", resultQuery);
        return new CarFilterResponseDto(count, searchResults);
    }


    /**
     * Создаем запрос для поиска авто по фильтру.
     *
     * @param filter фильтр
     * @return запрос
     */
    private Query buildQueryForCarFindByFilterByOneCriteria(CarFilterRequestDto filter) {
        final Criteria criteria = new Criteria();
        addAndCriteria(criteria, filter.getCarIds(), "_id");
        addAndCriteria(criteria, filter.getColors(), "color");
        addAndCriteria(criteria, filter.getBadges(), "badge");
        addAndCriteria(criteria, filter.getBadgeDetails(), "badgeDetail");
        addAndCriteria(criteria, filter.getFuelTypes(), "fuelType");
        addAndCriteria(criteria, filter.getManufacturers(), "manufacturer");
        addAndCriteria(criteria, filter.getModels(), "model");
        addAndCriteria(criteria, filter.getOfficeCityStates(), "officeCityState");
        addAndCriteria(criteria, filter.getTransmissions(), "transmission");


        if (!ObjectUtils.isEmpty(filter.getPriceLessThan())) {
            criteria.and(PRICE_FIELD_NAME).lte(filter.getPriceLessThan());
        }
        if (!ObjectUtils.isEmpty(filter.getPriceMoreThan())) {
            criteria.and(PRICE_FIELD_NAME).gte(filter.getPriceMoreThan());
        }

        if (!ObjectUtils.isEmpty(filter.getYearLessThan())) {
            criteria.and(YEAR_FIELD_NAME).lte(filter.getYearLessThan());
        }
        if (!ObjectUtils.isEmpty(filter.getYearMoreThan())) {
            criteria.and(YEAR_FIELD_NAME).gte(filter.getYearMoreThan());
        }
        if (!ObjectUtils.isEmpty(filter.getMileageLessThan())) {
            criteria.and(MILEAGE_FIELD_NAME).lte(filter.getMileageLessThan());
        }
        if (!ObjectUtils.isEmpty(filter.getMileageMoreThan())) {
            criteria.and(MILEAGE_FIELD_NAME).gte(filter.getMileageMoreThan());
        }
        return query(criteria);
    }

    /**
     * Создаем запрос для поиска авто по фильтру с сортировкой.
     *
     * @param filter фильтр
     * @return запрос
     */
    private Query buildQueryForCarFindByFilterByLotsOfCriterias(CarFilterRequestDto filter) {
        Query query = new Query();
        addAndCriteria(query, filter.getCarIds(), "_id");
        addAndCriteria(query, filter.getColors(), "color");
        addAndCriteria(query, filter.getBadges(), "badge");
        addAndCriteria(query, filter.getBadgeDetails(), "badgeDetail");
        addAndCriteria(query, filter.getFuelTypes(), "fuelType");
        addAndCriteria(query, filter.getManufacturers(), "manufacturer");
        addAndCriteria(query, filter.getModels(), "model");
        addAndCriteria(query, filter.getOfficeCityStates(), "officeCityState");
        addAndCriteria(query, filter.getTransmissions(), "transmission");

        if (!ObjectUtils.isEmpty(filter.getYearLessThan())) {
            query.addCriteria(Criteria.where(YEAR_FIELD_NAME).lte(filter.getYearLessThan()));
        }
        if (!ObjectUtils.isEmpty(filter.getYearMoreThan())) {
            query.addCriteria(Criteria.where(YEAR_FIELD_NAME).gte(filter.getYearMoreThan()));
        }

        if (!ObjectUtils.isEmpty(filter.getPriceLessThan())) {
            query.addCriteria(Criteria.where(PRICE_FIELD_NAME).lte(filter.getPriceLessThan()));
        }
        if (!ObjectUtils.isEmpty(filter.getPriceMoreThan())) {
            query.addCriteria(Criteria.where(PRICE_FIELD_NAME).gte(filter.getPriceMoreThan()));
        }

        if (!ObjectUtils.isEmpty(filter.getMileageLessThan())) {
            query.addCriteria(Criteria.where(MILEAGE_FIELD_NAME).lte(filter.getMileageLessThan()));
        }
        if (!ObjectUtils.isEmpty(filter.getMileageMoreThan())) {
            query.addCriteria(Criteria.where(MILEAGE_FIELD_NAME).gte(filter.getMileageMoreThan()));
        }

        //сортировка
        if (!ObjectUtils.isEmpty(filter.getSortField()) && Objects.nonNull(filter.getSortAscMode())) {
            var sortField = filter.getSortField();
            var sortMode = filter.getSortAscMode() ? Sort.Direction.ASC : Sort.Direction.DESC;
            query.with(Sort.by(sortMode, sortField));
        }
        return query;
    }

    /**
     * Добавление условия к запросу.
     *
     * @param criteria         критерий для дополнения
     * @param filterCollection список значений
     * @param fieldName        название поля
     */
    public void addAndCriteria(Criteria criteria, Collection<?> filterCollection, String
            fieldName) {
        if (!CollectionUtils.isEmpty(filterCollection)) {
            criteria.and(fieldName).in(filterCollection);
        }
    }

    /**
     * Добавление условия к запросу.
     *
     * @param filterCollection список значений
     * @param fieldName        название поля
     */
    public void addAndCriteria(Query query, Collection<?> filterCollection, String
            fieldName) {
        if (!CollectionUtils.isEmpty(filterCollection)) {
            query.addCriteria(Criteria.where(fieldName).in(filterCollection));
        }
    }
}
