package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.TruckDto;
import org.hofftech.parking.model.dto.ParcelDto;

@Slf4j
public class TruckGridService {
    private final TruckDto truckDto;

    public TruckGridService(int width, int height) {
        this.truckDto = new TruckDto(width, height);
    }

    public boolean canPlacePackage(ParcelDto pkg, int startRow, int startCol) {
        log.debug("Проверка размещения пакета на позиции: ({}, {})", startRow, startCol);
        return truckDto.canPlaceParcel(pkg, startRow, startCol);
    }

    public void placePackage(ParcelDto pkg, int startRow, int startCol) {
        truckDto.placeParcel(pkg, startRow, startCol);
    }

    public void print() {
        truckDto.print();
    }

    public char[][] getTruckDto() {
        return truckDto.getGrid();
    }
}
