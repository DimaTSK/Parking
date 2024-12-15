package org.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.parking.model.dto.TruckCapacityDto;
import org.parking.model.dto.ParcelDto;

import java.util.List;
@Slf4j
public class TruckService {
    private final TruckCapacityDto capacity;
    private final TruckGridService grid;

    public TruckService(TruckCapacityDto capacity) {
        this.capacity = capacity;
        this.grid = new TruckGridService(capacity.getWidth(), capacity.getHeight());
    }

    public void packPackages(List<ParcelDto> parcelDtos) {
        log.info("Началось размещение пакетов. Всего пакетов: {}", parcelDtos.size());
        parcelDtos.forEach(pkg -> {
            boolean placed = false;

            for (int i = 0; i <= capacity.getHeight() - pkg.getHeight(); i++) {
                if (placed) break;
                for (int j = 0; j <= capacity.getWidth() - pkg.getWidth(); j++) {
                    if (grid.canPlacePackage(pkg, i, j)) {
                        grid.placePackage(pkg, i, j);
                        log.debug("Пакет успешно размещён на позиции: ({}, {})", i, j);
                        placed = true;
                        break;
                    }
                }
            }

            if (!placed) {
                log.warn("Не удалось разместить пакет:\n{}", String.join("\n", pkg.getLines()));
            }
        });
        log.info("Завершено размещение пакетов.");
    }

    public void print() {
        grid.print();
    }
}