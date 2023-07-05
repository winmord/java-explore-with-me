package ru.practicum.main.validation;

public class PagingParametersChecker {
    private PagingParametersChecker() {
        throw new IllegalStateException("Utility class");
    }

    public static void check(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Некорректные параметры пагинации");
        }
    }
}
