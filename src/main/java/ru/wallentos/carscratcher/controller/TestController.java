package ru.wallentos.carscratcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.wallentos.carscratcher.dto.CalculatorRequestDto;
import ru.wallentos.carscratcher.dto.CalculatorResponseDto;
import ru.wallentos.carscratcher.service.CalculatorService;
import ru.wallentos.carscratcher.service.MoneyRateRestService;

@RestController
@RequestMapping("test/api/ui")
public class TestController {
    private CalculatorService calculatorService;
    private MoneyRateRestService moneyRateRestService;

    @Autowired
    public TestController(CalculatorService calculatorService, MoneyRateRestService moneyRateRestService) {
        this.calculatorService = calculatorService;
        this.moneyRateRestService = moneyRateRestService;
    }

    @CrossOrigin
    @GetMapping("/calculate-car")
    public CalculatorResponseDto calculateCarByFullRequest(
            @RequestParam(required = false) Integer yearMonth,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) Integer volume) {
        var requestDto = CalculatorRequestDto.builder()
                .yearMonth(yearMonth).originalPrice(price).volume(volume).build();
        return calculatorService.calculateKoreaCarPrice(requestDto);
    }

    @CrossOrigin
    @GetMapping("calculator-rates")
    public ResponseEntity<String> getMoneyConversionRates() {
        return ResponseEntity.ok(String.format("""
                Курс расчёта:
                %s
                """, moneyRateRestService.getCalculationRatesInRublesMap()));
    }
}
