package org.hofftech.parking.service.strategy.impl;

import lombok.AllArgsConstructor;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.service.strategy.PackingStrategy;

import java.util.List;

/**
 * Стратегия упаковки, распределяющая упаковки по отдельным грузовикам.
 * Использует {@link TruckService} для добавления упаковок в индивидуальные грузовики.
 */
@AllArgsConstructor
public class IndividualTruckStrategy implements PackingStrategy {
    private final TruckService truckService;

    /**
     * Добавляет список упаковок в отдельные грузовики, используя переданные аргументы.
     *
     * @param parcels           Список упаковок для добавления.
     * @param useEasyAlgorithm  Флаг, указывающий, использовать ли простой алгоритм упаковки.
     * @param useEvenAlgorithm  Флаг, указывающий, использовать ли алгоритм равномерного распределения.
     * @param trucksFromArgs    Список грузовиков, переданных через аргументы.
     * @return Список грузовиков с добавленными упаковками.
     */
    @Override
    public List<Truck> addPackages(List<Parcel> parcels, boolean useEasyAlgorithm, boolean useEvenAlgorithm, List<String> trucksFromArgs) {
        return truckService.addPackagesToIndividualTrucks(parcels, trucksFromArgs);
    }
}
