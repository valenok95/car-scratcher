package ru.wallentos.carscratcher.exception;

/**
 * Ошибка, если запрошенный автомобиль не найден.
 */
public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(String message) {
        super(message);
    }
}
