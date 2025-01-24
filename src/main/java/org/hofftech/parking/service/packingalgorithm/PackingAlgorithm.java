package org.hofftech.parking.service.packingalgorithm;

import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;

import java.util.List;
/**
 * Интерфейс для алгоритмов упаковки посылок в грузовики.
 * Предоставляет метод для добавления списка посылок в список грузовиков с возможностью
 * выбора различных алгоритмов упаковки и конфигурации грузовиков.
 */
public interface PackingAlgorithm {
    List<Truck> addParcels(List<Parcel> parcels, boolean useEasyAlgorithm, boolean useEvenAlgorithm, List<String> trucksFromArgs);
}