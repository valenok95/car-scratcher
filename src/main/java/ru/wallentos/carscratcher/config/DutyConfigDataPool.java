package ru.wallentos.carscratcher.config;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Конфиги на единые ставки таможенных пошлин.
 */
public class DutyConfigDataPool {
    private static final int NEW_CAR_PRICE_DUTY_1 = 8500;
    private static final Map.Entry<Double, Double> NEW_CAR_PRICE_FLAT_RATE_1 =
            new AbstractMap.SimpleEntry<>(0.54, 2.5);
    private static final int NEW_CAR_PRICE_DUTY_2 = 16700;
    private static final Map.Entry<Double, Double> NEW_CAR_PRICE_FLAT_RATE_2 =
            new AbstractMap.SimpleEntry<>(0.48, 3.5);
    private static final int NEW_CAR_PRICE_DUTY_3 = 42300;
    private static final Map.Entry<Double, Double> NEW_CAR_PRICE_FLAT_RATE_3 =
            new AbstractMap.SimpleEntry<>(0.48, 5.5);
    private static final int NEW_CAR_PRICE_DUTY_4 = 84500;
    private static final Map.Entry<Double, Double> NEW_CAR_PRICE_FLAT_RATE_4 =
            new AbstractMap.SimpleEntry<>(0.48, 7.5);
    private static final int NEW_CAR_PRICE_DUTY_5 = 169000;
    private static final Map.Entry<Double, Double> NEW_CAR_PRICE_FLAT_RATE_5 =
            new AbstractMap.SimpleEntry<>(0.48, 15d);
    public static final Map.Entry<Double, Double> NEW_CAR_PRICE_MAX_FLAT_RATE =
            new AbstractMap.SimpleEntry<>(0.48, 20d);
    private static final int NORMAL_CAR_ENGINE_VOLUME_DUTY_1 = 1000;
    private static final double NORMAL_CAR_PRICE_FLAT_RATE_1 = 1.5;
    private static final int NORMAL_CAR_ENGINE_VOLUME_DUTY_2 = 1500;
    private static final double NORMAL_CAR_PRICE_FLAT_RATE_2 = 1.7;
    private static final int NORMAL_CAR_ENGINE_VOLUME_DUTY_3 = 1800;
    private static final double NORMAL_CAR_PRICE_FLAT_RATE_3 = 2.5;
    private static final int NORMAL_CAR_ENGINE_VOLUME_DUTY_4 = 2300;
    private static final double NORMAL_CAR_PRICE_FLAT_RATE_4 = 2.7;
    private static final int NORMAL_CAR_ENGINE_VOLUME_DUTY_5 = 3000;
    private static final double NORMAL_CAR_PRICE_FLAT_RATE_5 = 3;
    public static final double NORMAL_CAR_PRICE_FLAT_RATE_MAX = 3.6;
    private static final int OLD_CAR_ENGINE_VOLUME_DUTY_1 = 1000;
    private static final double OLD_CAR_PRICE_FLAT_RATE_1 = 3;
    private static final int OLD_CAR_ENGINE_VOLUME_DUTY_2 = 1500;
    private static final double OLD_CAR_PRICE_FLAT_RATE_2 = 3.2;
    private static final int OLD_CAR_ENGINE_VOLUME_DUTY_3 = 1800;
    private static final double OLD_CAR_PRICE_FLAT_RATE_3 = 3.5;
    private static final int OLD_CAR_ENGINE_VOLUME_DUTY_4 = 2300;
    private static final double OLD_CAR_PRICE_FLAT_RATE_4 = 4.8;
    private static final int OLD_CAR_ENGINE_VOLUME_DUTY_5 = 3000;
    private static final double OLD_CAR_PRICE_FLAT_RATE_5 = 5;
    public static final double OLD_CAR_PRICE_FLAT_RATE_MAX = 5.7;

    /**
     * Карта рассчёта размера пошлины для нового автомобиля.
     */
    public static final Map<Integer, Map.Entry<Double, Double>> NEW_CAR_CUSTOMS_MAP = new LinkedHashMap<>() {
        {
            put(NEW_CAR_PRICE_DUTY_1, NEW_CAR_PRICE_FLAT_RATE_1);
            put(NEW_CAR_PRICE_DUTY_2, NEW_CAR_PRICE_FLAT_RATE_2);
            put(NEW_CAR_PRICE_DUTY_3, NEW_CAR_PRICE_FLAT_RATE_3);
            put(NEW_CAR_PRICE_DUTY_4, NEW_CAR_PRICE_FLAT_RATE_4);
            put(NEW_CAR_PRICE_DUTY_5, NEW_CAR_PRICE_FLAT_RATE_5);
        }
    };
    /**
     * Карта рассчёта размера пошлины для автомобиля от 3 до 5 лет.
     */
    public static final Map<Integer, Double> NORMAL_CAR_CUSTOMS_MAP = new LinkedHashMap<>() {
        {
            put(NORMAL_CAR_ENGINE_VOLUME_DUTY_1, NORMAL_CAR_PRICE_FLAT_RATE_1);
            put(NORMAL_CAR_ENGINE_VOLUME_DUTY_2, NORMAL_CAR_PRICE_FLAT_RATE_2);
            put(NORMAL_CAR_ENGINE_VOLUME_DUTY_3, NORMAL_CAR_PRICE_FLAT_RATE_3);
            put(NORMAL_CAR_ENGINE_VOLUME_DUTY_4, NORMAL_CAR_PRICE_FLAT_RATE_4);
            put(NORMAL_CAR_ENGINE_VOLUME_DUTY_5, NORMAL_CAR_PRICE_FLAT_RATE_5);
        }
    };
    /**
     * Карта рассчёта размера пошлины для автомобиля от 5 лет.
     */
    public static final Map<Integer, Double> OLD_CAR_CUSTOMS_MAP = new LinkedHashMap<>() {
        {
            put(OLD_CAR_ENGINE_VOLUME_DUTY_1, OLD_CAR_PRICE_FLAT_RATE_1);
            put(OLD_CAR_ENGINE_VOLUME_DUTY_2, OLD_CAR_PRICE_FLAT_RATE_2);
            put(OLD_CAR_ENGINE_VOLUME_DUTY_3, OLD_CAR_PRICE_FLAT_RATE_3);
            put(OLD_CAR_ENGINE_VOLUME_DUTY_4, OLD_CAR_PRICE_FLAT_RATE_4);
            put(OLD_CAR_ENGINE_VOLUME_DUTY_5, OLD_CAR_PRICE_FLAT_RATE_5);
        }
    };

}
