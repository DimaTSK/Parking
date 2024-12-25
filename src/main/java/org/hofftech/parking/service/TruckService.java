package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.entity.Truck;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.factory.TruckFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TruckService {
    private final ParcelLoadingService parcelLoadingService;
    private final TruckFactory truckFactory;
    private final ParcelService parcelService;

    public TruckService(ParcelLoadingService parcelLoadingService, TruckFactory truckFactory, ParcelService parcelService) {
        this.parcelLoadingService = parcelLoadingService;
        this.truckFactory = truckFactory;
        this.parcelService = parcelService;
    }

    public List<Truck> addParcelsToMultipleTrucks(List<ParcelDto> parcelDtoList, int maxTrucks, Boolean evenAlg) {
        log.info("Начало размещения упаковок. Всего упаковок: {}", parcelDtoList.size());

        List<Truck> truckEntities = evenAlg ? truckFactory.createTrucks(maxTrucks) : truckFactory.createTrucks(1);
        if (evenAlg) {
            parcelLoadingService.loadParcelsEvenly(parcelDtoList, truckEntities);
        } else {
            parcelService.placeParcels(parcelDtoList, truckEntities, maxTrucks);
        }

        log.info("Посылки размещены, количество грузовиков: {}", truckEntities.size());
        return truckEntities;
    }

    public List<Truck> addParcelsToIndividualTrucks(List<ParcelDto> parcelDtos) {
        List<Truck> truckEntities = new ArrayList<>();
        for (ParcelDto pkg : parcelDtos) {
            Truck truck = new Truck();
            parcelService.addParcels(truck, pkg);
            truckEntities.add(truck);
            log.info("Упаковка {} добавлена в новый грузовик.", pkg.getId());
        }
        return truckEntities;
    }
}

