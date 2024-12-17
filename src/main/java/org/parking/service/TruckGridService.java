package org.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.parking.model.dto.TruckDto;
import org.parking.model.dto.ParcelDto;

@Slf4j
public class TruckGridService {
    private final TruckDto grid;

    public TruckGridService(int width, int height) {
        this.grid = new TruckDto(width, height);
    }

    public boolean canPlacePackage(ParcelDto pkg, int startRow, int startCol) {
        log.debug("Проверка размещения пакета на позиции: ({}, {})", startRow, startCol);
        return grid.canPlaceParcel(pkg, startRow, startCol);
    }

    public void placePackage(ParcelDto pkg, int startRow, int startCol) {
        grid.placeParcel(pkg, startRow, startCol);
    }

    public void print() {
        grid.print();
    }

    public char[][] getGrid() {
        return grid.getGrid();
    }
}
