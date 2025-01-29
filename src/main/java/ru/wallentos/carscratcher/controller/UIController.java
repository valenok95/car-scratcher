package ru.wallentos.carscratcher.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.wallentos.carscratcher.dto.CarFilterRequestDto;
import ru.wallentos.carscratcher.dto.CarFilterResponseDto;
import ru.wallentos.carscratcher.dto.EncarDto;
import ru.wallentos.carscratcher.dto.WDType;
import ru.wallentos.carscratcher.exception.CarNotFoundException;
import ru.wallentos.carscratcher.repository.EncarRepository;
import ru.wallentos.carscratcher.service.EncarScratchService;

@RestController
@RequestMapping("api/ui")
public class UIController {
    private final EncarScratchService encarScratchService;
    private final EncarRepository encarRepository;

    @Autowired
    public UIController(EncarScratchService encarScratchService, EncarRepository encarRepository) {
        this.encarScratchService = encarScratchService;
        this.encarRepository = encarRepository;
    }

    @CrossOrigin
    @GetMapping("/get-car-by-id/{id}")
    public ResponseEntity<EncarDto.CarDto> getCarById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(encarRepository.findCarById(id));
        } catch (CarNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @CrossOrigin
    @GetMapping("/search-cars-by-filter")
    public CarFilterResponseDto searchCarsByFilterGetRequest(
            @RequestParam(required = false) List<Long> carIds,
            @RequestParam(required = false) List<String> manufacturers,
            @RequestParam(required = false) List<String> models,
            @RequestParam(required = false) List<String> badges,
            @RequestParam(required = false) List<String> badgeDetails,
            @RequestParam(required = false) List<String> transmissions,
            @RequestParam(required = false) List<String> fuelTypes,
            @RequestParam(required = false) Integer yearMoreThan,
            @RequestParam(required = false) Integer yearLessThan,
            @RequestParam(required = false) Integer mileageMoreThan,
            @RequestParam(required = false) Integer mileageLessThan,
            @RequestParam(required = false) List<String> colors,
            @RequestParam(required = false) Integer priceMoreThan,
            @RequestParam(required = false) Integer priceLessThan,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Boolean sortAscMode,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) WDType wdType,
            @RequestParam(required = false) List<String> officeCityStates) {
        var filter =
                CarFilterRequestDto.builder().carIds(carIds).manufacturers(manufacturers).models(models).badges(badges).badgeDetails(badgeDetails).transmissions(transmissions).fuelTypes(fuelTypes).yearMoreThan(yearMoreThan).yearLessThan(yearLessThan).mileageMoreThan(mileageMoreThan).mileageLessThan(mileageLessThan).colors(colors).priceMoreThan(priceMoreThan).priceLessThan(priceLessThan).limit(limit).skip(skip).sortAscMode(sortAscMode).sortField(sortField).officeCityStates(officeCityStates).wdType(wdType).build();
        return encarScratchService.findCarsByFilter(filter);
    }

    //MONGODB_URI=mongodb://localhost:27017/test
    @GetMapping("/update-cache")
    public void updateCache() {
        encarScratchService.refreshCache();
    }

    @CrossOrigin
    @GetMapping("/get-mark-list")
    public List<String> getMarkList() {
        return encarScratchService.getMarkList();
    }

    @CrossOrigin
    @GetMapping("/get-model-list-by-mark/{markName}")
    public List<String> getModelListByMarkName(@PathVariable String markName) {
        return encarScratchService.getModelListByMarkName(markName);
    }
}
