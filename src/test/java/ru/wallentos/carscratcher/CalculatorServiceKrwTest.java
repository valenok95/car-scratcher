package ru.wallentos.carscratcher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.wallentos.carscratcher.dto.CalculatorRequestDto;
import ru.wallentos.carscratcher.dto.CalculatorResponseDto;
import ru.wallentos.carscratcher.service.CalculatorService;

@SpringBootTest()
@ActiveProfiles("unit")
public class CalculatorServiceKrwTest {
    @Autowired
    CalculatorService calculatorService;
    @Test
    void executeTest(){
        var inputData = CalculatorRequestDto.builder().carId(12345).yearMonth(200712).build();
        var expectedResultData =
                CalculatorResponseDto.builder().carId(12345).carCategory(3).build();
        
        CalculatorResponseDto actualResultData =
                calculatorService.calculateKoreaCarPrice(inputData);

        Assertions.assertEquals(expectedResultData,actualResultData);
        
        
    }
    
    
}
