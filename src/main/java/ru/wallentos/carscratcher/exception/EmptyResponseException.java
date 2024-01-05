package ru.wallentos.carscratcher.exception;

/**
 * Ошибка, возникающая при получении пустого ответа.
 */
public class EmptyResponseException extends RuntimeException {
    public EmptyResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
