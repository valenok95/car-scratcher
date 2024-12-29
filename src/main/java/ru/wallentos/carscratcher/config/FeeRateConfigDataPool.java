package ru.wallentos.carscratcher.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Конфиги по ставкам таможенных сборов.
 */
public class FeeRateConfigDataPool {
    private static final int CUSTOMS_VALUE_1 = 200_000;
    private static final int FEE_RATE_1 = 1067;
    private static final int CUSTOMS_VALUE_2 = 450_000;
    private static final int FEE_RATE_2 = 2134;
    private static final int CUSTOMS_VALUE_3 = 1_200_000;
    private static final int FEE_RATE_3 = 4269;
    private static final int CUSTOMS_VALUE_4 = 2_700_000;
    private static final int FEE_RATE_4 = 11_746;
    private static final int CUSTOMS_VALUE_5 = 4_200_000;
    private static final int FEE_RATE_5 = 16_524;
    private static final int CUSTOMS_VALUE_6 = 5_500_000;
    private static final int FEE_RATE_6 = 21_344;
    private static final int CUSTOMS_VALUE_7 = 7_000_000;
    private static final int FEE_RATE_7 = 27_540;
    public static final int LAST_FEE_RATE = 30000;

    /**
     * Карта рассчёта таможенной стоимости.
     */
    public static final Map<Integer, Integer> FEE_RATE_MAP = new LinkedHashMap<>() {
        {
            put(CUSTOMS_VALUE_1, FEE_RATE_1);
            put(CUSTOMS_VALUE_2, FEE_RATE_2);
            put(CUSTOMS_VALUE_3, FEE_RATE_3);
            put(CUSTOMS_VALUE_4, FEE_RATE_4);
            put(CUSTOMS_VALUE_5, FEE_RATE_5);
            put(CUSTOMS_VALUE_6, FEE_RATE_6);
            put(CUSTOMS_VALUE_7, FEE_RATE_7);
        }
    };
}
