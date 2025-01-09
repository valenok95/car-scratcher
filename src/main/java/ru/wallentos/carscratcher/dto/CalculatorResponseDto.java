package ru.wallentos.carscratcher.dto;

import java.util.Locale;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculatorResponseDto {
    /**
     * Возрастная категория авто.
     * 1- до трех лет
     * 2- от трех до пяти лет (не включительно)
     * 3- начиная с 5 лет
     */
    private CarCategory carCategory;
    /**
     * Таможенный сбор.
     */
    private double feeRate;
    /**
     * Таможенная пошлина
     */
    private double duty;
    /**
     * Стоимость автомобиля без таможни.
     */
    private int firstPriceInRubles;
    /**
     * Утилизационный сбор.
     */
    private int recyclingFee;
    /**
     * Валютная надбавка в рублях.
     */
    private double extraPayAmountValutePart;

    /**
     * Рублёвая надбавка в рублях.
     */
    private int extraPayAmountRublePart;
    /**
     * Итоговая цена в рублях. (Первичная цена + таможенные/утиль сборы + надбавки)
     */
    private int resultPriceInRubles;
    /**
     * Локация доставки. (для результата)
     */
    private String location;


    @Override
    public String toString() {
        return String.format(Locale.FRANCE, """
                        Итого: <b>%,.0f руб.</b>
                                                
                        Стоимость автомобиля с учетом доставки и оформления:
                        %,.0fруб.
                                                
                        Таможенная пошлина и утилизационный сбор:
                        %,.0fруб.
                                           
                        Итоговая стоимость указана за автомобиль %s и включает все расходы, в том числе процедуру таможенной очистки.""",
                resultPriceInRubles, firstPriceInRubles + extraPayAmountValutePart,
                feeRate + duty + recyclingFee, location);
    }

}
