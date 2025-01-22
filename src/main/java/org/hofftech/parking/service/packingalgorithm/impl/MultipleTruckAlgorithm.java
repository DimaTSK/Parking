package org.hofftech.parking.service.packingalgorithm.impl;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.service.packingalgorithm.PackingAlgorithm;

import java.util.List;
/**
 * Класс реализации алгоритма упаковки для нескольких грузовиков.
 */
@RequiredArgsConstructor
public class MultipleTruckAlgorithm implements PackingAlgorithm {
    private final TruckService truckService;
    /**
     * Добавляет посылки в несколько грузовиков.
     */
    @Override
    public List<Truck> addParcels(List<Parcel> parcels, boolean useEasyAlgorithm, boolean useEvenAlgorithm, List<String> trucksFromArgs) {
        return truckService.addParcelsToMultipleTrucks(parcels, useEvenAlgorithm, trucksFromArgs);
    }
}