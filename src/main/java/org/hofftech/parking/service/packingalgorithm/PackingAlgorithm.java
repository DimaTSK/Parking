package org.hofftech.parking.service.packingalgorithm;

import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;

import java.util.List;

public interface PackingAlgorithm {
    List<Truck> addParcels(List<Parcel> parcels, boolean useEasyAlgorithm, boolean useEvenAlgorithm, List<String> trucksFromArgs);
}