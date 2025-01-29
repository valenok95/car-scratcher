package ru.wallentos.carscratcher.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TranslatorConfig {
    /**
     * Конфиги по переводчику с корейского.
     */
    private static final String KOREAN_HYUNDAI = "현대";
    private static final String ENGLISH_HYUNDAI = "Hyundai";

    private static final String KOREAN_GENESIS= "제네시스";
    private static final String ENGLISH_GENESIS = "Genesis";

    private static final String KOREAN_BENZ="벤츠";
    private static final String ENGLISH_BENZ ="Mercedes-Benz";

    /**
     * Словарь автопроизводителей для перевода.
     */
    public static final Map<String, String> MANUFACTURER_TRANSLATE_MAP = new LinkedHashMap<>() {
        {
            put(KOREAN_HYUNDAI, ENGLISH_HYUNDAI);
            put(KOREAN_GENESIS, ENGLISH_GENESIS);
            put(KOREAN_BENZ, ENGLISH_BENZ);
        }
    };
}
