package ru.wallentos.carscratcher.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.wallentos.carscratcher.dto.EncarSearchResponseDto;

public interface EncarDataRepository extends MongoRepository<EncarSearchResponseDto, String> {

    
    public EncarSearchResponseDto insert(EncarSearchResponseDto encarSearchResponseDto);

}