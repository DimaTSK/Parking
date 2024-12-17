package org.hofftech.parking.service;

import org.hofftech.parking.model.dto.TruckCapacityDto;
import org.hofftech.parking.model.dto.ParcelDto;


import java.util.List;

public class ParcelPackerService {
    private static final int TRUCK_WIDTH = 6; // Ширина грузовика
    private static final int TRUCK_LENGTH = 6; // Длина грузовика

    private TruckService truckService;


    public ParcelPackerService(TruckService truckService) {
        this.truckService = truckService;
    }

    public void packPackages(List<ParcelDto> parcelDtos) {
        TruckCapacityDto truckCapacityDto = new TruckCapacityDto(TRUCK_WIDTH, TRUCK_LENGTH);
        truckService.packPackages(parcelDtos);
    }
}