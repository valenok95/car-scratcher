package ru.wallentos.carscratcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @PostMapping("test-calculator")
    public ResponseEntity<CalculatorResponseDto> calculateCarByFullRequest(@RequestBody CalculatorRequestDto calculatorRequestDto) {
        return ResponseEntity.ok(calculatorService.calculateKoreaCarPrice(calculatorRequestDto));
    }
    @CrossOrigin
    @GetMapping("calculator-rates")
    public ResponseEntity<String> getMoneyConversionRates() {
        return ResponseEntity.ok(String.format("""
                Курс расчёта:
                %s
                """,moneyRateRestService.getCalculationRatesInRublesMap()));
    }
}
