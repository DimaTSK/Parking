package org.hofftech.parking.factory;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.service.packingalgorithm.PackingAlgorithm;
import org.hofftech.parking.service.packingalgorithm.impl.IndividualTruckAlgorithm;
import org.hofftech.parking.service.packingalgorithm.impl.MultipleTruckAlgorithm;


/**
 * Фабрика алгоритмов упаковки для отправлений, использующая службы грузовиков.
 * <p>
 * Данная фабрика предоставляет метод для создания стратегии упаковки в зависимости от заданного условия.
 * </p>
 */
@RequiredArgsConstructor
public class ParcelAlgorithmFactory {
    private final TruckService truckService;

    /**
     * Создает стратегию упаковки в зависимости от переданного параметра.
     *
     * @param ifEasyAlgorithm флаг, определяющий, использовать ли простой алгоритм упаковки
     * @return экземпляр {@link PackingAlgorithm}, соответствующий выбранной стратегии
     */
    public PackingAlgorithm createStrategy(boolean ifEasyAlgorithm) {
        if (ifEasyAlgorithm) {
            return new IndividualTruckAlgorithm(truckService);
        } else {
            return new MultipleTruckAlgorithm(truckService);
        }
    }
}
