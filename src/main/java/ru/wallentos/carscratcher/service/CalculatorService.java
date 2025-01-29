package ru.wallentos.carscratcher.service;

import static ru.wallentos.carscratcher.config.DutyConfigDataPool.NEW_CAR_CUSTOMS_MAP;
import static ru.wallentos.carscratcher.config.DutyConfigDataPool.NEW_CAR_PRICE_MAX_FLAT_RATE;
import static ru.wallentos.carscratcher.config.DutyConfigDataPool.NORMAL_CAR_CUSTOMS_MAP;
import static ru.wallentos.carscratcher.config.DutyConfigDataPool.NORMAL_CAR_PRICE_FLAT_RATE_MAX;
import static ru.wallentos.carscratcher.config.DutyConfigDataPool.OLD_CAR_CUSTOMS_MAP;
import static ru.wallentos.carscratcher.config.DutyConfigDataPool.OLD_CAR_PRICE_FLAT_RATE_MAX;
import static ru.wallentos.carscratcher.config.FeeRateConfigDataPool.FEE_RATE_MAP;
import static ru.wallentos.carscratcher.config.FeeRateConfigDataPool.LAST_FEE_RATE;
import static ru.wallentos.carscratcher.config.RecycleFeeConfigDataPool.NEW_BIG_CAR_RECYCLING_FEE;
import static ru.wallentos.carscratcher.config.RecycleFeeConfigDataPool.NEW_CAR_RECYCLING_FEE;
import static ru.wallentos.carscratcher.config.RecycleFeeConfigDataPool.NEW_MID_CAR_RECYCLING_FEE;
import static ru.wallentos.carscratcher.config.RecycleFeeConfigDataPool.OLD_BIG_CAR_RECYCLING_FEE;
import static ru.wallentos.carscratcher.config.RecycleFeeConfigDataPool.OLD_CAR_RECYCLING_FEE;
import static ru.wallentos.carscratcher.config.RecycleFeeConfigDataPool.OLD_MID_CAR_RECYCLING_FEE;
import static ru.wallentos.carscratcher.dto.Currency.KRW;
import static ru.wallentos.carscratcher.dto.Currency.RUB;
import static ru.wallentos.carscratcher.dto.Currency.USD;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.wallentos.carscratcher.dto.CalculatorRequestDto;
import ru.wallentos.carscratcher.dto.CalculatorResponseDto;
import ru.wallentos.carscratcher.dto.CarCategory;
import ru.wallentos.carscratcher.dto.Currency;

/**
 * Калькулятор для расчёта стоимости автомобиля, утиль сбора и таможни.
 */
@Service
@Slf4j
public class CalculatorService {
    /**
     * Активирован ли механизм двойной конвертации.
     */
    @Value("${ru.wallentos.carscratcher.calculator.enable-double-convertation:}")
    private boolean enableDoubleConvertation;

    /**
     * Активирован ли механизм динамической валютной части KRW.
     */
    @Value("${ru.wallentos.carscratcher.calculator.enable-dynamic-valute-part:true}")
    private boolean enableDynamicValutePart;

    /**
     * Статическая рублёвая надбавка для Кореи.
     */
    @Value("${ru.wallentos.carscratcher.calculator.extra-pay-corea.krw:}")
    private int extraPayAmountInKoreaKrw;

    /**
     * Статическая рублёвая надбавка для Кореи.
     */
    @Value("${ru.wallentos.carscratcher.calculator.extra-pay-corea.rub:}")
    private int extraPayAmountInKoreaRub;

    /**
     * Карта для динамического расчёта валютной надбавки KRW.
     * Цена в долларах: Надбавка в вонах
     */
    @Value("#{${ru.wallentos.carscratcher.calculator.dynamic-krw-valute-part-map:}}")
    private Map<Integer, Integer> dynamicKrwValutePartMap;

    private MoneyRateRestService moneyRateRestService;

    @Autowired
    public CalculatorService(MoneyRateRestService moneyRateRestService) {
        this.moneyRateRestService = moneyRateRestService;
        moneyRateRestService.refreshExchangeRates();
    }

    /**
     * Расчёт стоимости автомобиля под ключ с учётом всех расходов для корейского рынка.
     *
     * @param calculatorRequest
     * @return
     */
    public CalculatorResponseDto calculateKoreaCarPrice(CalculatorRequestDto calculatorRequest) {
        double priceInEuro = convertMoneyToEuro(calculatorRequest.getOriginalPrice(), KRW);
        CarCategory carCategory = calculateCarCategoryByYearMonth(calculatorRequest.getYearMonth());
        int firstPriceInRubles =
                calculateFirstCarPriceInRublesByKrw(calculatorRequest.getOriginalPrice());
        int extraPayRublePart = executeRubExtraPayAmountByCurrency(KRW);
        int rawExtraPayInKrw = getRawValutePartInKrw(firstPriceInRubles);//первичная надбавка KRW
        double extraPayKrwPart =
                executeFinalExtraPayValutePartInRublesByKrw(rawExtraPayInKrw); //надбавка KRW в рублях
        double feeRate =
                calculateFeeRateFromCarPriceInRubles(priceInEuro);
        double duty = calculateDutyInRubles(priceInEuro, carCategory, calculatorRequest.getVolume());
        int recyclingFee = calculateRecyclingFeeInRubles(carCategory, calculatorRequest.getVolume());

        return CalculatorResponseDto.builder()
                .carCategory(carCategory)
                // sanctionCar не относится к калькулятору, проверять снаружи.
                .feeRate(feeRate)
                .duty(duty)
                .recyclingFee(recyclingFee)
                .firstPriceInRubles(firstPriceInRubles)
                //теперь считаем валютную надбавку в зависимости от настройки (динамичная либо статик конфиг)
                .extraPayAmountRublePart(extraPayRublePart)
                .resultPriceInRubles((int) (firstPriceInRubles + feeRate + duty + recyclingFee + extraPayRublePart + extraPayKrwPart))
                .extraPayAmountValutePart(extraPayKrwPart)
                .location(executeLocation(KRW))
                .build();
    }


    /**
     * Вычисляем категорию по месяцу и году.
     * 1- до трех лет
     * 2- от трех до пяти лет (не включительно)
     * 3- начиная с 5 лет
     *
     * @return возрастная категория
     */
    private CarCategory calculateCarCategoryByYearMonth(int yearMonth) {
        int year = yearMonth / 100;
        int month = yearMonth % 100;
        LocalDate oldDate = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());
        return calculateCarCategoryByLocalDate(oldDate);
    }


    /**
     * Вычисляем категорию по дате автомобиля.
     * 1- до трех лет
     * 2- от трех до пяти лет
     * 3- свыше пяти лет
     *
     * @return возрастная категория
     */
    private CarCategory calculateCarCategoryByLocalDate(LocalDate localDate) {
        Period period = Period.between(localDate, LocalDate.now());
        int carYearsOld = period.getYears();
        int carMonthOld = period.getMonths();
        if (carYearsOld >= 5) {
            return CarCategory.OLD_CAR;
        } else if (carYearsOld > 2 || (carYearsOld == 2 && carMonthOld == 11)) {
            return CarCategory.NORMAL_CAR;
        } else {
            return CarCategory.NEW_CAR;
        }
    }


    /**
     * Сбор за таможенные операции.
     * Первая составляющая для конечного расчёта.
     *
     * @param rawCarPriceInEuro
     * @return
     */
    private double calculateFeeRateFromCarPriceInRubles(double rawCarPriceInEuro) {
        double carPriceInRubles = convertMoneyFromEuro(rawCarPriceInEuro, RUB);
        int resultFeeRate = LAST_FEE_RATE;
        for (Map.Entry<Integer, Integer> pair : FEE_RATE_MAP.entrySet()) {
            if (carPriceInRubles < pair.getKey()) {
                resultFeeRate = pair.getValue();
                break;
            }
        }
        return resultFeeRate;
    }

    /**
     * Перевести в валюту из Евро.
     *
     * @param count      количество в Евро.
     * @param toCurrency конечная валюта.
     * @return количество в валюте.
     */
    private double convertMoneyFromEuro(double count, Currency toCurrency) {
        return count * moneyRateRestService.getCurrencyAmountInOneEuroCbr(toCurrency);
    }

    /**
     * Перевести в Евро из определенной валюты.
     *
     * @param count        количество в валюте.
     * @param fromCurrency исходная валюта.
     * @return количество в Евро.
     */
    public double convertMoneyToEuro(double count, Currency fromCurrency) {
        return count / moneyRateRestService.getCurrencyAmountInOneEuroCbr(fromCurrency);
    }


    /**
     * Вычисляем таможенную пошлину авто.
     *
     * @param rawCarPriceInEuro стоимость авто
     * @param carCategory       возрастная категория авто
     * @return стоимость пошлины
     */
    private double calculateDutyInRubles(double rawCarPriceInEuro, CarCategory carCategory, int carVolume) {
        double dutyInEuro;
        switch (carCategory) {
            case NEW_CAR -> dutyInEuro = calculateNewCarDutyInEuro(rawCarPriceInEuro, carVolume);
            case NORMAL_CAR -> dutyInEuro = calculateNormalCarDutyInEuro(carVolume);
            default -> dutyInEuro = calculateOldCarDutyInEuro(carVolume); // OLD_CAR
        }
        return convertMoneyFromEuro(dutyInEuro, RUB);
    }


    /**
     * Вычисляем пошлину авто до 3 лет.
     *
     * @param carPriceInEuro стоимость авто
     * @return стоимость пошлины
     */
    private double calculateNewCarDutyInEuro(double carPriceInEuro, int carVolume) {
        double resultCarDuty = getMaxFromPair(NEW_CAR_PRICE_MAX_FLAT_RATE,
                carPriceInEuro,
                carVolume);
        for (Map.Entry<Integer, Map.Entry<Double, Double>> pair : NEW_CAR_CUSTOMS_MAP.entrySet()) {
            if (carPriceInEuro <= pair.getKey()) {
                resultCarDuty = getMaxFromPair(pair.getValue(), carPriceInEuro, carVolume);
                break;
            }
        }
        return resultCarDuty;
    }

    /**
     * Вычисляем пошлину авто от 3 до 5 лет.
     *
     * @param carVolume объем двигателя
     * @return стоимость пошлины
     */
    private double calculateNormalCarDutyInEuro(int carVolume) {
        double resultCarDuty = carVolume * NORMAL_CAR_PRICE_FLAT_RATE_MAX;
        for (Map.Entry<Integer, Double> pair : NORMAL_CAR_CUSTOMS_MAP.entrySet()) {
            if (carVolume <= pair.getKey()) {
                resultCarDuty = carVolume * pair.getValue();
                break;
            }
        }
        return resultCarDuty;
    }

    /**
     * Вычисляем пошлину авто от 5 лет.
     *
     * @param carVolume объем двигателя
     * @return стоимость пошлины
     */
    private double calculateOldCarDutyInEuro(int carVolume) {
        double resultCarDuty = carVolume * OLD_CAR_PRICE_FLAT_RATE_MAX;
        for (Map.Entry<Integer, Double> pair : OLD_CAR_CUSTOMS_MAP.entrySet()) {
            if (carVolume <= pair.getKey()) {
                resultCarDuty = carVolume * pair.getValue();
                break;
            }
        }
        return resultCarDuty;
    }


    /**
     * Считаем наибольшее значение пошлины, либо по кубам, либо по умножению на процент.
     */
    private double getMaxFromPair(Map.Entry<Double, Double> pair, double carPriceInEuro, int carVolume) {
        double priceByPercent = pair.getKey() * carPriceInEuro;
        double priceByVolume = pair.getValue() * carVolume;
        return Math.max(priceByVolume, priceByPercent);
    }


    /**
     * Рассчёт утилизационного сбора.
     *
     * @param carCategory категория авто
     * @param volume      объем двигателя.
     * @return стоимость утилизационного сбора
     */
    private int calculateRecyclingFeeInRubles(CarCategory carCategory, int volume) {
        if (carCategory.equals(CarCategory.NEW_CAR)) {
            return calculateNewCarRecyclingFeeByVolume(volume);
        } else {
            return calculateOldCarRecyclingFeeByVolume(volume);
        }
    }

    /**
     * Расчёт утилизационного сбора для новых авто.
     *
     * @param volume объем двигателя.
     * @return Утиль сбор в рублях.
     */
    private int calculateNewCarRecyclingFeeByVolume(int volume) {
        if (volume <= 3000) {
            return NEW_CAR_RECYCLING_FEE;
        } else if (volume <= 3500) {
            return NEW_MID_CAR_RECYCLING_FEE;
        } else {
            return NEW_BIG_CAR_RECYCLING_FEE;
        }
    }

    /**
     * Расчёт утилизационного сбора для старых авто.
     *
     * @param volume объем двигателя.
     * @return Утиль сбор в рублях.
     */
    private int calculateOldCarRecyclingFeeByVolume(int volume) {
        if (volume <= 3000) {
            return OLD_CAR_RECYCLING_FEE;
        } else if (volume <= 3500) {
            return OLD_MID_CAR_RECYCLING_FEE;
        } else {
            return OLD_BIG_CAR_RECYCLING_FEE;
        }
    }


    /**
     * Вычислить первичную стоимость автомобиля в рублях. (Корейский рынок)
     *
     * @param originalPriceInKrw оригинальная стоимость авто в вонах.
     * @return стоимость авто в рублях.
     */
    private int calculateFirstCarPriceInRublesByKrw(int originalPriceInKrw) {
        double result;
        log.debug("Вычисляем первичную стоимость автомобиля в рублях для стоимости {} KRW",
                originalPriceInKrw);
        // двойная конвертация при включенной настройке. ЦЕНУ В ВОНАХ делим на спец. курс KRW/RUB
        if (enableDoubleConvertation) {
            //курс KRW/RUB считается через курс naver USD/KRW * курс расчета USD/RUB
            double krwRubDoubleConvertationRate =
                    moneyRateRestService.getRublesAmountInCurrencyForCalculationRate(USD) / moneyRateRestService.getCbrUsdKrwMinusCorrection();
            result = originalPriceInKrw * krwRubDoubleConvertationRate;
            log.debug("""
                            Режим двойной конвертации:
                            Стоимость автомобиля {} {} поделённая на курс USD/KRW-коррекция {} и умноженная на ручной курс USD/RUB {} = {} RUB""", originalPriceInKrw
                    , KRW,
                    moneyRateRestService.getCbrUsdKrwMinusCorrection(),
                    moneyRateRestService.getRublesAmountInCurrencyForCalculationRate(USD), result);
        } else {
            double calculationRate =
                    moneyRateRestService.getRublesAmountInCurrencyForCalculationRate(KRW);
            result = originalPriceInKrw * calculationRate;
            log.debug("Режим стандартной конвертации:" +
                            "Стоимость автомобиля {} {} * {} = {} RUB", originalPriceInKrw, KRW,
                    calculationRate, result);
        }
        return (int) result;
    }


    /**
     * Гибкий расчет первичной надбавки KRW в зависимости от конфигов.
     * Далее она будет использована для расчёта финальной валютной надбавки с переводом в рубли.
     *
     * @param priceInRubles - первичная цена в рублях
     * @return первичная валютная часть надбавки в KRW.
     */
    private int getRawValutePartInKrw(double priceInRubles) {
        if (enableDynamicValutePart) {
            double calculationUsdRubRate = moneyRateRestService.getRublesAmountInCurrencyForCalculationRate(USD);
            double priceInUsd = priceInRubles / calculationUsdRubRate;
            int extraPayAmountKoreaKrwResult =
                    getDynamicValutePartInKrwByUsdPrice(priceInUsd);
            log.debug("""
                    Устанавливаем динамическую валютную надбавку для KRW:
                    Цена в $ - это Цена в рублях {} поделить на ручной курс USD {} = {}$
                    Соответствующая валютная надбавка - {} KRW.
                    """, priceInRubles, calculationUsdRubRate, priceInUsd, extraPayAmountKoreaKrwResult);
            return extraPayAmountKoreaKrwResult;
        } else {
            return extraPayAmountInKoreaKrw;
        }
    }

    /**
     * Определить динамическую валютную надбавку исходя из стоимости авто в $.
     * Цена в долларах меньше чем ключ мапы - берем значение мапы в качестве надбавки
     *
     * @param priceInUsd цена в USD
     * @return возвращает в KRW
     */
    private int getDynamicValutePartInKrwByUsdPrice(double priceInUsd) {
        int dynamicValutePartResult = 0;
        for (Map.Entry<Integer, Integer> pair : dynamicKrwValutePartMap.entrySet()) {
            if (priceInUsd < pair.getKey()) {
                dynamicValutePartResult = pair.getValue();
                break;
            }
        }
        return dynamicValutePartResult;
    }

    /**
     * Рассчитываем доп взносы. Рублёвая часть. Брокерские расходы, СВХ, СБКТС.
     */
    private int executeRubExtraPayAmountByCurrency(Currency currency) {
        switch (currency) {
            case KRW, USD:
                return extraPayAmountInKoreaRub;
            case CNY: // дополнить при добавлении Китая
            default:
                return 0;
        }
    }

    /**
     * Финальный расчёт валютной части доп. взносов в рублях, на основании первичной валютной части.
     *
     * @param rawExtraPayInKrw первичная валютная надбавка в KRW.
     * @return Итоговая валютная надбавка в рублях.
     */
    private double executeFinalExtraPayValutePartInRublesByKrw(int rawExtraPayInKrw) {
        log.debug("Финальный расчёт валютной надбавки, первичная надбавка - {} KRW", rawExtraPayInKrw);
        return enableDoubleConvertation ? getExtraKrwPayAmountDoubleConvertationInRubles(rawExtraPayInKrw) :
                getExtraPayKrwAmountNormalConvertationInRub(rawExtraPayInKrw);
    }

    /**
     * Расчет финальной валютной надбавки KRW в рублях через двойную конвертацию.
     *
     * @param rawExtraPayInKrw количество в валюте
     * @return возвращаем в рублях.
     */
    private double getExtraKrwPayAmountDoubleConvertationInRubles(int rawExtraPayInKrw) {
        double calculationUsdRubRate = moneyRateRestService.getRublesAmountInCurrencyForCalculationRate(USD);
        double usdKrwMinusCorrectionRate = moneyRateRestService.getCbrUsdKrwMinusCorrection();
        double priceInUsd = rawExtraPayInKrw / usdKrwMinusCorrectionRate;
        double result = priceInUsd * calculationUsdRubRate;
        log.debug("""
                        В режиме двойной конвертации.
                        Валютная надбавка в USD: надбавка {} KRW * курс-коррекция {} = {} USD
                        Валютная надбавка в рублях: {} USD * ручной курс {} = {} RUB
                        """, rawExtraPayInKrw, usdKrwMinusCorrectionRate, priceInUsd,
                priceInUsd, calculationUsdRubRate, result);
        return result;
    }

    /**
     * Считаем доп взносы переведенные в рубли по обычной конвертации ДЛЯ КОРЕИ.
     *
     * @param rawExtraPayInKrw - исходная валютная надбавка в KRW
     * @return валютная надбавка в рублях.
     */
    private double getExtraPayKrwAmountNormalConvertationInRub(int rawExtraPayInKrw) {
        double calculationKrwRubRate =
                moneyRateRestService.getRublesAmountInCurrencyForCalculationRate(KRW);
        double result;
        result = rawExtraPayInKrw * calculationKrwRubRate;
        log.debug("""
                В режиме нормальной конвертации в рублях:
                Надбавка {} KRW * ручной курс {} = {} RUB
                """, rawExtraPayInKrw, calculationKrwRubRate, result);
        return result;
    }

    /**
     * Определяем до куда доставка.
     *
     * @param currency валюта.
     * @return локация доставки.
     */
    public String executeLocation(Currency currency) {
        switch (currency) {
            case KRW:
                return "до Владивостока";
            case CNY:
                return "до Уссурийска";
            default:
                return "неизвестно до куда";
        }
    }
}


