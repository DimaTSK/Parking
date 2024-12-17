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
        System.out.print("+");
        for (int j = 0; j < truckDto.getGrid()[0].length; j++) {
            System.out.print("-");
        }
        System.out.println("+");

        for (int i = truckDto.getGrid().length - 1; i >= 0; i--) {
            System.out.print("|");
            for (int j = 0; j < truckDto.getGrid()[i].length; j++) {
                System.out.print(truckDto.getGrid()[i][j]);
            }
            System.out.println("|");
        }

        System.out.print("+");
        for (int j = 0; j < truckDto.getGrid()[0].length; j++) {
            System.out.print("-");
        }
        System.out.println("+");
    }

    public char[][] getTruckDto() {
        return truckDto.getGrid();
    }
}
