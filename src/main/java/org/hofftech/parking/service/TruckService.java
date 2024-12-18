package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.TruckCapacityDto;
import org.hofftech.parking.model.dto.ParcelDto;

import java.util.List;

@Slf4j
public class TruckService {
    private final TruckCapacityDto capacity;
    private final TruckGridService truckGridService;

    public TruckService(TruckCapacityDto capacity, TruckGridService truckGridService) {
        this.capacity = capacity;
        this.truckGridService = truckGridService;
    }

    public void packPackages(List<ParcelDto> parcelDtos) {
        log.info("Началось размещение пакетов. Всего пакетов: {}", parcelDtos.size());

        for (int idx = 0; idx < parcelDtos.size(); idx++) {
            ParcelDto pkg = parcelDtos.get(idx);
            boolean placed = false;

            for (int i = 0; i <= capacity.height() - pkg.getHeight(); i++) {
                if (placed) break;
                for (int j = 0; j <= capacity.width() - pkg.getWidth(); j++) {
                    if (truckGridService.canPlacePackage(pkg, i, j)) {
                        truckGridService.placePackage(pkg, i, j);
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
