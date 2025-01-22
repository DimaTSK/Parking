package org.hofftech.parking.factory;

import lombok.AllArgsConstructor;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.service.packingalgorithm.PackingAlgorithm;
import org.hofftech.parking.service.packingalgorithm.impl.IndividualTruckAlgorithm;
import org.hofftech.parking.service.packingalgorithm.impl.MultipleTruckAlgorithm;


@AllArgsConstructor
public class ParcelAlgorithmFactory {
    private final TruckService truckService;
    public PackingAlgorithm createStrategy(boolean ifEasyAlgorithm) {
        if (ifEasyAlgorithm) {
            return new IndividualTruckAlgorithm(truckService);
        } else {
            return new MultipleTruckAlgorithm(truckService);
        }
    }
}