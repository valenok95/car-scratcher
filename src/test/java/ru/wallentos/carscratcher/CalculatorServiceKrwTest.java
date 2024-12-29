package ru.wallentos.carscratcher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.wallentos.carscratcher.dto.CalculatorRequestDto;
import ru.wallentos.carscratcher.dto.CalculatorResponseDto;
import ru.wallentos.carscratcher.dto.CarCategory;
import ru.wallentos.carscratcher.service.CalculatorService;

@SpringBootTest()
@ActiveProfiles("unit")
public class CalculatorServiceKrwTest {
    @Autowired
    CalculatorService calculatorService;
    @Test
    void executeTest(){
        var inputData = CalculatorRequestDto.builder().yearMonth(200712).build();
        var expectedResultData =
                CalculatorResponseDto.builder().carCategory(CarCategory.OLD_CAR)
                        .feeRate(1067.0)
                        .duty(0.0)
                        .firstPriceInRubles(0.0)
                        .recyclingFee(5200)
                        .extraPayAmountRublePart(100000.0)
                        .extraPayAmountValutePart(114725.35933147631)
                        .resultPriceInRubles(220992.35933147633)
                        .location("до Владивостока")
                        .build();
        
        CalculatorResponseDto actualResultData =
                calculatorService.calculateKoreaCarPrice(inputData);

        Assertions.assertEquals(expectedResultData,actualResultData);
        
        
    }
    
    
}
