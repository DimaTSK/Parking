package org.hofftech.parking.service.strategy;

import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;

import java.util.List;

/**
 * Интерфейс стратегии упаковки, определяющий способ распределения упаковок по грузовикам.
 */
public interface PackingStrategy {
    /**
     * Добавляет упаковки в список грузовиков, используя выбранную стратегию.
     *
     * @param parcels           Список упаковок для добавления.
     * @param useEasyAlgorithm  Флаг, указывающий, использовать ли простой алгоритм упаковки.
     * @param useEvenAlgorithm  Флаг, указывающий, использовать ли алгоритм равномерного распределения.
     * @param trucksFromArgs    Список грузовиков, переданных через аргументы.
     * @return Список грузовиков с добавленными упаковками.
     */
    List<Truck> addPackages(List<Parcel> parcels, boolean useEasyAlgorithm, boolean useEvenAlgorithm, List<String> trucksFromArgs);
}
