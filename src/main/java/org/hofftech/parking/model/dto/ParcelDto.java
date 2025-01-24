package org.hofftech.parking.model.dto;

import lombok.Data;

import java.util.List;
/**
 * Класс DTO (Data Transfer Object) для представления информации о посылке.
 * <p>
 * Предоставляет данные, связанные с посылкой, включая её название, форму, символ и начальную позицию.
 */
@Data
public class ParcelDto {
    private String name;
    private List<String> shape;
    private char symbol;
    private PositionDto startPosition;
}