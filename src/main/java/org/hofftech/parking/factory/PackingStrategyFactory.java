package org.hofftech.parking.factory;

import lombok.AllArgsConstructor;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.service.strategy.impl.IndividualTruckStrategy;
import org.hofftech.parking.service.strategy.impl.MultipleTruckStrategy;
import org.hofftech.parking.service.strategy.PackingStrategy;

/**
 * Фабрика стратегий упаковки грузовиков.
 * <p>
 * Этот класс отвечает за создание экземпляров {@link PackingStrategy} на основе переданных параметров.
 * Использует {@link TruckService} для инициализации стратегий упаковки.
 * </p>
 *
 * @author
 * @version 1.0
 */
@AllArgsConstructor
public class PackingStrategyFactory {

    /**
     * Сервис для управления грузовиками, используемый стратегиями упаковки.
     */
    private final TruckService truckService;

    /**
     * Возвращает соответствующую стратегию упаковки в зависимости от переданного флага.
     * <p>
     * Если {@code useEasyAlgorithm} равно {@code true}, возвращается {@link IndividualTruckStrategy}.
     * В противном случае возвращается {@link MultipleTruckStrategy}.
     * </p>
     *
     * @param useEasyAlgorithm флаг, указывающий, какую стратегию использовать
     * @return экземпляр {@link PackingStrategy} в соответствии с переданным флагом
     */
    public PackingStrategy getStrategy(boolean useEasyAlgorithm) {
        if (useEasyAlgorithm) {
            return new IndividualTruckStrategy(truckService);
        } else {
            return new MultipleTruckStrategy(truckService);
        }
    }
}
