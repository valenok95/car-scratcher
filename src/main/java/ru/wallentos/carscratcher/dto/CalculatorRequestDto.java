package ru.wallentos.carscratcher.dto;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import ru.wallentos.carscratcher.Currency;

/**
 * Запрос на расчёт детализации по авто.
 */
@Data
@Builder
public class CalculatorRequestDto {
    /**
     * Дата выпуска в формате YYYYMM. Пример: "202401"
     */
    private int yearMonth;
    /**
     * Объем двигателя автомобиля.
     */
    private int volume;
    /**
     * Стоимость авто в валюте.
     */
    private int originalPrice;
   // private Province province;

    @Override
    public String toString() {
        return String.format(Locale.FRANCE, """
                Дата выпуска: %s.
                Стоимость: %d валютных единиц\s
                Объем двигателя: %d cc""", yearMonth, originalPrice, volume);


    }
}
