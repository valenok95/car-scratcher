package ru.wallentos.carscratcher.dto;

/**
 * Возрастная категория авто.
 */
public enum CarCategory {
    OLD_CAR("начиная с 5 лет"),
    NORMAL_CAR("от трех до пяти лет (не включительно)"),
    NEW_CAR("до трех лет");
    private final String description;

    CarCategory(String description) {
        this.description = description;
    }
}
