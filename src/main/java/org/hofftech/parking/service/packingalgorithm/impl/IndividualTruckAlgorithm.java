package org.hofftech.parking.service.packingalgorithm.impl;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.service.packingalgorithm.PackingAlgorithm;

import java.util.List;

@RequiredArgsConstructor
public class IndividualTruckAlgorithm implements PackingAlgorithm {

    private final TruckService truckService;

    @Override
    public List<Truck> addParcels(List<Parcel> parcels, boolean isEasyAlgorithm, boolean isEvenAlgorithm, List<String> trucksFromArgs) {
        return truckService.addParcelsToIndividualTrucks(parcels, trucksFromArgs);
    }
}