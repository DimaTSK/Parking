package org.hofftech.parking.service.strategy;

import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;

import java.util.List;


public interface DefaultPackingStrategy {
    List<Truck> addPackages(List<Parcel> parcels, boolean useEasyAlgorithm, boolean useEvenAlgorithm, List<String> trucksFromArgs);
}