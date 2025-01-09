package ru.wallentos.carscratcher.mapper;

import org.mapstruct.Mapper;
import ru.wallentos.carscratcher.dto.EncarDto;
import ru.wallentos.carscratcher.dto.EncarSearchResponseEntity;

@Mapper(componentModel = "spring", uses = CarMapper.class)
public interface EncarResponseMapper {

    EncarDto toDto(EncarSearchResponseEntity responseEntity);

}
