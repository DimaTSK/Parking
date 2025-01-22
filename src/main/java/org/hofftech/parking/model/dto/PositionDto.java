package org.hofftech.parking.model.dto;

import lombok.Data;
/**
 * Класс DTO (Data Transfer Object) для представления позиции на двумерной плоскости.
 * <p>
 * Предоставляет координаты точки с осями X и Y, используемые для различных визуальных и логических операций в системе.
 */
@Data
public class PositionDto {
    private int x;
    private int y;
}