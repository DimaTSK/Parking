package org.hofftech.parking.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) для представления информации о грузовике.
 * Используется для передачи данных между различными слоями приложения.
 */
@Data
public class TruckDto {

    /**
     * Уникальный идентификатор грузовика.
     */
    private int truckId;

    /**
     * Размер грузовика. Может представлять собой категории размеров, такие как "Маленький", "Средний", "Большой" и т.д.
     */
    private String truckSize;

    /**
     * Список пакетов, находящихся в данном грузовике.
     * Представляет собой список объектов {@link ParcelDto}, содержащих информацию о каждом пакете.
     */
    private List<ParcelDto> packages;
}
