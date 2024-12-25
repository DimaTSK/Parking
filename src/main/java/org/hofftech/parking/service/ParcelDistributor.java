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

            for (ParcelDto pkg : group) {
                if (!parcelService.addParcels(truck, pkg)) {
                    throw new RuntimeException("Не хватает количества грузовиков для определения!");
                }
            }
        }

        log.info("Посылки распределены по грузовикам.");
    }

    public void placeParcels(List<ParcelDto> parcelDtoList, List<Truck> truckEntities, int maxTrucks) {
        for (ParcelDto pkg : parcelDtoList) {
            boolean placed = false;
            for (Truck truck : truckEntities) {
                if (parcelService.addParcels(truck, pkg)) {
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                if (truckEntities.size() < maxTrucks) {
                    Truck newTruck = new Truck();
                    if (parcelService.addParcels(newTruck, pkg)) {
                        truckEntities.add(newTruck);
                    } else {
                        throw new RuntimeException("Упаковка " + pkg.getType() + " с ID " + pkg.getId() +
                                " не может быть размещена даже в новом грузовике.");
                    }
                } else {
                    throw new RuntimeException("Превышен лимит грузовиков: " + maxTrucks);
                }
            }
        }
    }
}
