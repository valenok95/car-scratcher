package ru.wallentos.carscratcher.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.wallentos.carscratcher.dto.EncarDto;

public interface EncarDataRepository extends MongoRepository<EncarDto, String> {

    
    public EncarDto insert(EncarDto encarDto);

}