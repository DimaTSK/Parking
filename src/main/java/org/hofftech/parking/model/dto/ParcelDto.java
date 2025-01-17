package org.hofftech.parking.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) для представления информации о пакете.
 * Используется для передачи данных между различными слоями приложения.
 */
@Data
public class ParcelDto {

    /**
     * Название пакета.
     */
    private String name;

    /**
     * Форма пакета, представленная списком строк.
     * Каждая строка описывает одну линию формы пакета.
     */
    private List<String> shape;

    /**
     * Символ, используемый для отображения пакета.
     */
    private char symbol;

    /**
     * Начальная позиция пакета.
     * Представляет собой координаты на плоскости (x, y).
     */
    private PositionDto startPosition;

    /**
     * Внутренний класс для представления позиции на плоскости.
     * Используется для указания начальной позиции пакета.
     */
    @Data
    public static class PositionDto {

        /**
         * Координата по оси X.
         */
        private int x;

        /**
         * Координата по оси Y.
         */
        private int y;
    }
}
