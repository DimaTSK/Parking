package org.hofftech.parking.utill;

import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.entity.Truck;
import org.hofftech.parking.service.ParcelService;

public class ParcelPlacer {
    private final ParcelService parcelService;

    public ParcelPlacer(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    public boolean addParcelToTruck(Truck truck, ParcelDto pkg) {
        return parcelService.addParcels(truck, pkg);
    }
}