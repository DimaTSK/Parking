package org.hofftech.parking.service.strategy;

import lombok.AllArgsConstructor;
import org.hofftech.parking.service.TruckService;


@AllArgsConstructor
public class PackingStrategyFactory {
    private final TruckService truckService;

    public DefaultPackingStrategy getStrategy(boolean useEasyAlgorithm) {
        if (useEasyAlgorithm) {
            return new IndividualTruckStrategy(truckService);
        } else {
            return new MultipleTruckStrategy(truckService);
        }
    }
}