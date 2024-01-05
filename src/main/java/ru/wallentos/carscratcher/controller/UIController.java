package ru.wallentos.carscratcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.wallentos.carscratcher.dto.CarFilterRequestDto;
import ru.wallentos.carscratcher.dto.CarFilterResponseDto;
import ru.wallentos.carscratcher.service.EncarScratchService;

@RestController
@RequestMapping("api/ui")
public class UIController {
    private final EncarScratchService encarScratchService;

    @Autowired
    public UIController(EncarScratchService encarScratchService) {
        this.encarScratchService = encarScratchService;
    }

    @PostMapping("/search-cars-by-filter")
    public CarFilterResponseDto searchCarsByFilter(@RequestBody CarFilterRequestDto filter) {
        return encarScratchService.findCarsByFilter(filter);
    }
}
