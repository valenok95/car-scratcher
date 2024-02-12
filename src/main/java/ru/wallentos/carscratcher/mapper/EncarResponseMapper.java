package ru.wallentos.carscratcher.mapper;

import org.mapstruct.Mapper;
import ru.wallentos.carscratcher.dto.EncarSearchResponseDto;
import ru.wallentos.carscratcher.dto.EncarSearchResponseEntity;

@Mapper(componentModel = "spring", uses = CarMapper.class)
public interface EncarResponseMapper {
    EncarSearchResponseDto toDto(EncarSearchResponseEntity responseEntity);

}
