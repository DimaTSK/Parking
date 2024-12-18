package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.TruckCapacityDto;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.TruckDto;

import java.util.List;

@Slf4j
public class TruckService {
    private final TruckCapacityDto capacity;

    public TruckService(TruckCapacityDto capacity) {
        this.capacity = capacity;
    }

    public boolean canPlacePackage(ParcelDto pkg, int startRow, int startCol, TruckDto truckDto) {
        log.debug("Проверка размещения пакета на позиции: ({}, {})", startRow, startCol);
        return canPlaceParcel(pkg, startRow, startCol, truckDto);
    }

    public void placePackage(ParcelDto pkg, int startRow, int startCol, TruckDto truckDto) {
        if (!canPlaceParcel(pkg, startRow, startCol, truckDto)) {
            throw new IllegalArgumentException("Не удается разместить посылку на указанной позиции.");
        }

        for (int i = 0; i < pkg.getHeight(); i++) {
            for (int j = 0; j < pkg.getWidth(); j++) {
                truckDto.getGrid()[startRow + i][startCol + j] = pkg.getLines()[i].charAt(j);
            }
        }
    }

    public boolean canPlaceParcel(ParcelDto parcel, int startRow, int startCol, TruckDto truckDto) {
        if (startRow < 0 || startCol < 0 || startRow + parcel.getHeight() > truckDto.getGrid().length || startCol + parcel.getWidth() > truckDto.getGrid()[0].length) {
            return false;
        }

        for (int i = 0; i < parcel.getHeight(); i++) {
            for (int j = 0; j < parcel.getWidth(); j++) {
                if (truckDto.getGrid()[startRow + i][startCol + j] != ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    public void print(TruckDto truckDto) {
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

    public void packPackages(List<ParcelDto> parcelDtos, TruckDto truckDto) {
        log.info("Началось размещение пакетов. Всего пакетов: {}", parcelDtos.size());

        for (ParcelDto pkg : parcelDtos) {
            boolean placed = false;

            for (int i = 0; i <= capacity.height() - pkg.getHeight(); i++) {
                if (placed) break;
                for (int j = 0; j <= capacity.width() - pkg.getWidth(); j++) {
                    if (canPlacePackage(pkg, i, j, truckDto)) {
                        placePackage(pkg, i, j, truckDto);
                        log.debug("Пакет успешно размещён на позиции: ({}, {})", i, j);
                        placed = true;
                        break;
                    }
                }
            }

            if (!placed) {
                log.warn("Не удалось разместить пакет:\n{}", String.join("\n", pkg.getLines()));
            }
        }

        log.info("Завершено размещение пакетов.");
    }
}
