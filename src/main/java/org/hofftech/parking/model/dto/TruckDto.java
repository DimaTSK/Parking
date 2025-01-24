package org.hofftech.parking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Класс DTO (Data Transfer Object) для транспортного средства типа грузовик.
 * <p>
 * Представляет данные, связанные с грузовиком, включая его идентификатор, размер и список груза.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TruckDto {
    private int truckId;
    private String truckSize;
    private List<ParcelDto> parcels;
}