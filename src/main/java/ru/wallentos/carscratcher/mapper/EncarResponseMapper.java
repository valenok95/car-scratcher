package ru.wallentos.carscratcher.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.wallentos.carscratcher.dto.EncarSearchResponseDto;
import ru.wallentos.carscratcher.dto.EncarSearchResponseEntity;

@Component
public class EncarResponseMapper {
    private final ModelMapper modelMapper;

    public EncarResponseMapper() {
        this.modelMapper = new ModelMapper();
/*        modelMapper.createTypeMap(EncarSearchResponseEntity.CarEntity.class,
                EncarSearchResponseDto.CarDto.class).addMappings(mapper -> {
            mapper.map(src-> src.getYear()/100,
                    EncarSearchResponseDto.CarDto::setYear);
                });*/
        /*        
            mapper.map(src-> src.getYear()*100,
                    EncarSearchResponseDto.CarDto::setMonth);
        */
    }

    public EncarSearchResponseDto toDto(EncarSearchResponseEntity responseEntity) {
        return modelMapper.map(responseEntity, EncarSearchResponseDto.class);
    }

    public EncarSearchResponseEntity toEntity(EncarSearchResponseDto responseDto) {
        return modelMapper.map(responseDto, EncarSearchResponseEntity.class);
    }
}
