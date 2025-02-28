package ru.wallentos.carscratcher.mapper;

import static ru.wallentos.carscratcher.config.TranslatorConfig.MANUFACTURER_TRANSLATE_MAP;

import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.wallentos.carscratcher.dto.EncarDto;
import ru.wallentos.carscratcher.dto.EncarSearchResponseEntity;
import ru.wallentos.carscratcher.dto.WDType;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(source = "badge", target = "wdType", qualifiedByName =
            "defineWDType")
    @Mapping(source = "price", target = "originalPrice", qualifiedByName =
            "convertKrwPrice")
    @Mapping(source = "manufacturer", target = "manufacturer", qualifiedByName =
            "defineManufacturer")
    EncarDto.CarDto toCarDto(EncarSearchResponseEntity.CarEntity responseEntity);

    @Named("defineWDType")
    static WDType defineWDTypeByBadge(String badge) {
        if (badge.contains("AWD") || badge.contains("4WD")) {
            return WDType.FWD;
        } else if (badge.contains("2WD")) {
            return WDType.TWD;
        } else {
            return WDType.UNKNOWN;
        }
    }
    @Named("convertKrwPrice")
    static int convertKrwPrice(int price) {
        return price * 10_000;
    }
    @Named("defineManufacturer")
    static String defineManufacturer(String manufacturerKoreanName) {
        String englishName = MANUFACTURER_TRANSLATE_MAP.get(manufacturerKoreanName);
        return Objects.nonNull(englishName) ? englishName : manufacturerKoreanName;
    }
}
