package ru.wallentos.carscratcher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.wallentos.carscratcher.dto.CalculatorRequestDto;
import ru.wallentos.carscratcher.dto.CalculatorResponseDto;
import ru.wallentos.carscratcher.dto.CarCategory;
import ru.wallentos.carscratcher.dto.Currency;
import ru.wallentos.carscratcher.service.CalculatorService;
import ru.wallentos.carscratcher.service.MoneyRateRestService;

@SpringBootTest()
@ActiveProfiles("unit")
public class CalculatorServiceKrwTest {
    @Autowired
    CalculatorService calculatorService;
    @MockBean
    MoneyRateRestService moneyRateRestService;

    @Test
    void executeTest() {
        Mockito.doReturn(1526d).when(moneyRateRestService).getCurrencyAmountInOneEuroCbr(Currency.KRW);
        Mockito.doReturn(106d).when(moneyRateRestService).getCurrencyAmountInOneEuroCbr(Currency.RUB);
        Mockito.doReturn(103d).when(moneyRateRestService).getRublesAmountInCurrencyForCalculationRate(Currency.USD);
        Mockito.doReturn(1421d).when(moneyRateRestService).getCbrUsdKrwMinusCorrection();

        var inputData = CalculatorRequestDto.builder().yearMonth(200712).build();
        var expectedResultData = CalculatorResponseDto.builder().carCategory(CarCategory.OLD_CAR)
                .feeRate(1067.0)
                .duty(0.0)
                .firstPriceInRubles(0)
                .recyclingFee(5200)
                .extraPayAmountRublePart(100000)
                .extraPayAmountValutePart(115974.66572836031)
                .resultPriceInRubles(222242)
                .location("до Владивостока")
                .build();

        CalculatorResponseDto actualResultData =
                calculatorService.calculateKoreaCarPrice(inputData);

        Assertions.assertEquals(expectedResultData, actualResultData);
    }
}
