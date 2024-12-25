package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.entity.Truck;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ParcelDistributor {
    private final ParcelService parcelService;

    public ParcelDistributor(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    public void distributeParcelsEvenly(List<ParcelDto> parcelDtos, List<Truck> truckEntities) {
        if (truckEntities.isEmpty()) {
            log.error("Количество грузовиков не может быть равно 0.");
            throw new IllegalArgumentException("Невозможно распределить посылки: нет грузовиков.");
        }

        int totalParcels = parcelDtos.size();
        int numberOfTrucks = truckEntities.size();
        int minParcelsPerTruck = totalParcels / numberOfTrucks;
        int extraParcels = totalParcels % numberOfTrucks;

        log.info("Распределяем {} посылок на {} грузовиков. Минимум в грузовике: {}, дополнительные посылки: {}",
                totalParcels, numberOfTrucks, minParcelsPerTruck, extraParcels);

        List<List<ParcelDto>> truckParcels = new ArrayList<>();
        for (int i = 0; i < numberOfTrucks; i++) {
            truckParcels.add(new ArrayList<>());
        }

        int currentTruckIndex = 0;
        for (ParcelDto pkg : parcelDtos) {
            truckParcels.get(currentTruckIndex).add(pkg);
            currentTruckIndex = (currentTruckIndex + 1) % numberOfTrucks;
        }

        for (int i = 0; i < numberOfTrucks; i++) {
            Truck truck = truckEntities.get(i);
            List<ParcelDto> group = truckParcels.get(i);

            log.info("Заполняем грузовик {} из пачки {} посылок.", i + 1, group.size());
            for (ParcelDto pkg : group) {
                if (!parcelService.addParcels(truck, pkg)) {
                    log.error("Не получилось определить посылку {} в грузовик {}.", pkg.getType(), i + 1);
                    throw new RuntimeException("Не хватает кол-ва грузовиков для определения!");
                }
            }
        }

        log.info("Посылки распределены по грузовикам.");
    }

    public void placeParcels(List<ParcelDto> parcelDtoList, List<Truck> truckEntities, int maxTrucks) {
        for (ParcelDto pkg : parcelDtoList) {
            log.info("Пытаемся разместить упаковку {} с ID {}", pkg.getType(), pkg.getId());
            boolean placed = false;
            for (Truck truck : truckEntities) {
                if (parcelService.addParcels(truck, pkg)) {
                    log.info("Упаковка {} с ID {} успешно размещена в существующем грузовике.", pkg.getType(), pkg.getId());
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                log.info("Упаковка {} с ID {} не поместилась. Создаём новый грузовик.", pkg.getType(), pkg.getId());
                if (truckEntities.size() < maxTrucks) {
                    Truck newTruck = new Truck();
                    if (parcelService.addParcels(newTruck, pkg)) {
                        truckEntities.add(newTruck);
                        log.info("Упаковка {} с ID {} размещена в новом грузовике.", pkg.getType(), pkg.getId());
                    } else {
                        log.error("Ошибка: упаковка {} с ID {} не может быть размещена даже в новом грузовике.", pkg.getType(), pkg.getId());
                    }
                } else {
                    log.error("Превышен установленный лимит грузовиков!");
                    throw new RuntimeException("Превышен лимит грузовиков: " + maxTrucks);
                }
            }
        }
    }
}