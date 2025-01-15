package org.hofftech.parking.factory;

import lombok.AllArgsConstructor;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.service.strategy.impl.IndividualTruckStrategy;
import org.hofftech.parking.service.strategy.impl.MultipleTruckStrategy;
import org.hofftech.parking.service.strategy.PackingStrategy;


@AllArgsConstructor
public class PackingStrategyFactory {
    private final TruckService truckService;

    public PackingStrategy getStrategy(boolean useEasyAlgorithm) {
        if (useEasyAlgorithm) {
            return new IndividualTruckStrategy(truckService);
        } else {
            return new MultipleTruckStrategy(truckService);
        }
    }
}