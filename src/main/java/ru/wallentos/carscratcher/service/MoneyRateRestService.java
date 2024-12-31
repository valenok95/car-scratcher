package ru.wallentos.carscratcher.service;

import static ru.wallentos.carscratcher.dto.Currency.CNY;
import static ru.wallentos.carscratcher.dto.Currency.EUR;
import static ru.wallentos.carscratcher.dto.Currency.KRW;
import static ru.wallentos.carscratcher.dto.Currency.RUB;
import static ru.wallentos.carscratcher.dto.Currency.USD;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.wallentos.carscratcher.dto.Currency;

/**
 * Сервис отвечающий за курсы валют.
 */
@Service
@Slf4j
@Data
public class MoneyRateRestService {
    private static final String VALUE = "Value";
    @Getter
    public Map<Currency, Double> calculationRatesInRublesMap = new HashMap<>();
    /**
     * Коэффициент, на который умножается курс ЦБ, чтобы получить курс расчёта автомобилей.
     */
    @Value("${ru.wallentos.carscratcher.exchange-api.exchange-coefficient}")
    public double exchangeCoefficient;
    /**
     * Значение вычитается из курса USD/KRW, чтобы получить курс для двойной конвертации.
     */
    @Value("${ru.wallentos.carworker.usd-krw-correction-rate:40}")
    public double usdKrwCorrectionRate;
    /**
     * Курс источника naver USD/KRW скорректированный для двойной конвертации.
     */
    @Getter
    private double cbrUsdKrwMinusCorrection;
    /**
     * Хост ЦБ.
     */
    @Value("${ru.wallentos.carscratcher.exchange-api.host-cbr}")
    private String cbrMethod;
    /**
     * Хост ПРОФИНАНС.
     */
    @Value("${ru.wallentos.carscratcher.exchange-api.host-profinance}")
    private String profinanceMethod;
    /**
     * Хост naver.
     */
    @Value("${ru.wallentos.carscratcher.exchange-api.host-naver}")
    private String naverMethod;
    /**
     * Сколько валюты содержится в евро.
     */
    private Map<Currency, Double> cbrRatesInEuroMap;
    private RestTemplate restTemplate;
    private ObjectMapper mapper;

    @Autowired
    public MoneyRateRestService(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    /**
     * Обновить все курсы валют.
     */
    public void refreshExchangeRates() {
        refreshCbrRateInEuroMap();
        refreshCalculationRateInRublesMap();
        refreshUsdKrwMinusCorrectionRate();
    }

    /**
     * Обновить курс ЦБ и хранить относительно ЕВРО.
     */
    private void refreshCbrRateInEuroMap() {
        ResponseEntity<String> response = restTemplate.getForEntity(cbrMethod, String.class);
        cbrRatesInEuroMap = generateCbrRatesInEuroMap(response.getBody());
        log.info("курс ЦБ в евро обновлён {}", cbrRatesInEuroMap);
    }

    /**
     * Обновить курс расчёта автомобиля (ЦБ с поправочным коэффициентом).
     * Хранить относительно рубля.
     */
    private void refreshCalculationRateInRublesMap() {
        cbrRatesInEuroMap.forEach((key, value) -> calculationRatesInRublesMap.put(key,
                cbrRatesInEuroMap.get(RUB) * exchangeCoefficient / value));

        // курс расчёта доллара к рублю получаем отдельно в profinance.ru
        Double profinanceUsdRubRate = getUsdRubProfinanceRate();
        if (Objects.nonNull(profinanceUsdRubRate)) {
            calculationRatesInRublesMap.put(USD,
                    profinanceUsdRubRate * exchangeCoefficient);
            log.info("курс расчёта для доллара установлен из источника profinance {} умноженный " +
                            "на КФ настройки {}",
                    profinanceUsdRubRate, exchangeCoefficient);
        }
        log.info("курс расчёта в рублях обновлён {}", calculationRatesInRublesMap);
    }

    /**
     * Получаем курс USD/KRW из источника naver, затем корректируем его на значение из настройки.
     */
    private void refreshUsdKrwMinusCorrectionRate() {
        cbrUsdKrwMinusCorrection = Double.parseDouble(getNaverRate()) - usdKrwCorrectionRate;
        log.info("Курс ЦБ USD/KRW минус коррекция ({}) обновлён: {}", usdKrwCorrectionRate,
                cbrUsdKrwMinusCorrection);
    }

    /**
     * Преобразовываем из JSON ЦБ курса в формат валют относительно EUR.
     */
    private Map<Currency, Double> generateCbrRatesInEuroMap(String jsonString) {
        double rubRateTmp;
        double usdRate;
        double krwRate;
        double cnyRate;
        try {
            var valutes = mapper.readTree(jsonString).get("Valute");

            rubRateTmp = valutes.get(EUR.name()).get(VALUE).asDouble();
            usdRate = rubRateTmp / valutes.get(USD.name()).get(VALUE).asDouble();
            cnyRate = rubRateTmp / valutes.get(CNY.name()).get(VALUE).asDouble();
            krwRate = rubRateTmp / valutes.get(KRW.name()).get(VALUE).asDouble() * 1000;

        } catch (JsonProcessingException e) {
            log.error("Ошибка при получении курсов валют ЦБ: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        Map<Currency, Double> cbrRatesMap = new HashMap<>();
        cbrRatesMap.put(RUB, rubRateTmp);
        cbrRatesMap.put(USD, usdRate);
        cbrRatesMap.put(CNY, cnyRate);
        cbrRatesMap.put(KRW, krwRate);
        return cbrRatesMap;
    }

    /**
     * Сколько валюты содержится в одном Евро из курса ЦБ.
     *
     * @param currency валюта
     * @return количество валюты в одном Евро.
     */
    public double getCurrencyAmountInOneEuroCbr(Currency currency) {
        return cbrRatesInEuroMap.get(currency);
    }

    /**
     * Сколько рублей содержится в валюте из курса расчёта.
     *
     * @param currency валюта
     * @return количество валюты в одном Евро.
     */
    public double getRublesAmountInCurrencyForCalculationRate(Currency currency) {
        return calculationRatesInRublesMap.get(currency);
    }

    /**
     * Получить курс доллара из источника profinance.ru
     *
     * @return курс USD/RUB из profinance.ru
     */
    private Double getUsdRubProfinanceRate() {
        try {
            Connection tokenConnection = Jsoup.connect(profinanceMethod).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
            String token = tokenConnection.execute().body();
            Connection rateConnection = Jsoup.connect(profinanceMethod).userAgent("Mozilla/5.0 (X11; Linux x86_64) " + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36").requestBody(String.format("1;SID=%s;A=;I=29;S=USD/RUB;\n", token));
            String rawRateString =
                    rateConnection.post().body().childNodes().get(0).toString().substring(30, 40);
            String rateString = parseProfinanceResponseToPositiveRate(rawRateString);
            return Double.parseDouble(rateString);
        } catch (IOException e) {
            log.error("Ошибка при получении курса USD/RUB из источника profinance {}", profinanceMethod);
            return null;
        }
    }

    /**
     * Вытащить курс доллара из profinance response со знаком ПЛЮС+  //A=-99.858;L
     */
    private String parseProfinanceResponseToPositiveRate(String rawProfinanceRate) {
        Pattern pattern = Pattern.compile("(\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(rawProfinanceRate);
        matcher.find();
        return matcher.group();
    }

    /**
     * Получаем курс корейской воны к доллару из источника naver.
     *
     * @return курс корейской воны к доллару из источника naver.
     */
    private String getNaverRate() {
        try {
            var document = Jsoup.connect(naverMethod).get();
            return document.getElementById("select_from").childNodes().get(3).attributes().asList().get(0).getValue();
        } catch (IOException e) {
            log.error("Ошибка при получении курса USD/KRW из источника naver {}", naverMethod);
            throw new RuntimeException(e);
        }
    }
}
