package org.hofftech.parking.service.strategy.impl;

import lombok.AllArgsConstructor;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.service.strategy.PackingStrategy;

import java.util.List;

@AllArgsConstructor
public class IndividualTruckStrategy implements PackingStrategy {
    private final TruckService truckService;

    @Override
    public List<Truck> addPackages(List<Parcel> parcels, boolean useEasyAlgorithm, boolean useEvenAlgorithm, List<String> trucksFromArgs) {
        return truckService.addPackagesToIndividualTrucks(parcels, trucksFromArgs);
    }
}