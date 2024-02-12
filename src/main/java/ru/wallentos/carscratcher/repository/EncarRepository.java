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
import org.springframework.util.StringUtils;
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
        mongoTemplate.dropCollection(ENCAR_RESULT_COLLECTION_NAME);
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
                ENCAR_RESULT_COLLECTION_NAME);
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
        addAndInCriteria(criteria, filter.getCarIds(), "_id");
        addAndInCriteria(criteria, filter.getColors(), "color");
        addAndInCriteria(criteria, filter.getBadges(), "badge");
        addAndInCriteria(criteria, filter.getBadgeDetails(), "badgeDetail");
        addAndInCriteria(criteria, filter.getFuelTypes(), "fuelType");
        addAndInCriteria(criteria, filter.getManufacturers(), "manufacturer");
        addAndInCriteria(criteria, filter.getModels(), "model");
        addAndInCriteria(criteria, filter.getOfficeCityStates(), "officeCityState");
        addAndInCriteria(criteria, filter.getTransmissions(), "transmission");


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
        addAndInCriteria(query, filter.getCarIds(), "_id");
        addAndInCriteria(query, filter.getColors(), "color");
        addAndInCriteria(query, filter.getBadges(), "badge");
        addAndInCriteria(query, filter.getBadgeDetails(), "badgeDetail");
        addAndInCriteria(query, filter.getFuelTypes(), "fuelType");
        addAndInCriteria(query, filter.getManufacturers(), "manufacturer");
        addAndInCriteria(query, filter.getModels(), "model");
        addAndInCriteria(query, filter.getOfficeCityStates(), "officeCityState");
        addAndInCriteria(query, filter.getTransmissions(), "transmission");
        if (Objects.nonNull(filter.getWdType())) {
            query.addCriteria(Criteria.where("wdType").is(filter.getWdType().name()));
        }

        addComparableFieldCriteria(query, filter.getYearLessThan(), filter.getYearMoreThan(),
                YEAR_FIELD_NAME);
        addComparableFieldCriteria(query, filter.getPriceLessThan(), filter.getPriceMoreThan(),
                PRICE_FIELD_NAME);
        addComparableFieldCriteria(query, filter.getMileageLessThan(), filter.getMileageMoreThan(),
                MILEAGE_FIELD_NAME);

        //сортировка
        if (!ObjectUtils.isEmpty(filter.getSortField()) && Objects.nonNull(filter.getSortAscMode())) {
            var sortField = filter.getSortField();
            var sortMode = Boolean.TRUE.equals(filter.getSortAscMode()) ? Sort.Direction.ASC : Sort.Direction.DESC;
            query.with(Sort.by(sortMode, sortField));
        }
        return query;
    }

    /**
     * Добавить фильтр по сравняемому полю. Больше-меньше.
     *
     * @param query
     * @param lessThan
     * @param moreThan
     * @param field
     */
    private void addComparableFieldCriteria(Query query, Integer lessThan, Integer moreThan, String field) {
        if (!ObjectUtils.isEmpty(lessThan) && !ObjectUtils.isEmpty(moreThan)) {
            query.addCriteria(Criteria.where(field).lte(lessThan).gte(moreThan));
        } else {
            if (!ObjectUtils.isEmpty(lessThan)) {
                query.addCriteria(Criteria.where(field).lte(lessThan));
            }
            if (!ObjectUtils.isEmpty(moreThan)) {
                query.addCriteria(Criteria.where(field).gte(moreThan));
            }
        }
    }

    /**
     * Добавление условия к запросу.
     *
     * @param criteria         критерий для дополнения
     * @param filterCollection список значений
     * @param fieldName        название поля
     */
    private void addAndInCriteria(Criteria criteria, Collection<?> filterCollection, String
            fieldName) {
        if (!CollectionUtils.isEmpty(filterCollection)) {
            criteria.and(fieldName).in(filterCollection);
        }
    }

    /**
     * Добавление условия к запросу список значений к полю.
     *
     * @param filterCollection список значений
     * @param fieldName        название поля
     */
    private void addAndInCriteria(Query query, Collection<?> filterCollection, String
            fieldName) {
        if (!CollectionUtils.isEmpty(filterCollection)) {
            query.addCriteria(Criteria.where(fieldName).in(filterCollection));
        }
    }

    /**
     * Добавление условия к запросу.
     *
     * @param value     значение
     * @param fieldName название поля
     */
    private void addAndIsCriteria(Query query, String value, String
            fieldName) {
        if (StringUtils.hasText(value)) {
            query.addCriteria(Criteria.where(fieldName).is(value));
        }
    }


    /**
     * Получить список моделей по марке из БД (distinct).
     *
     * @param markName производитель.
     */
    public List<String> getModelListByMarkName(String markName) {
        return mongoTemplate.findDistinct(
                new Query(
                        Criteria.where("manufacturer")
                                .in(markName)),
                "model", ENCAR_RESULT_COLLECTION_NAME, String.class);
    }

    /**
     * Получить список марок из БД (distinct).
     */
    public List<String> getMarkList() {
        return mongoTemplate.findDistinct(
                new Query(),
                "manufacturer", ENCAR_RESULT_COLLECTION_NAME, String.class);
    }
}
