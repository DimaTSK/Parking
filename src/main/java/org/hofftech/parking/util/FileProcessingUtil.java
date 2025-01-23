package org.hofftech.parking.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.factory.ParcelAlgorithmFactory;
import org.hofftech.parking.model.Order;
import org.hofftech.parking.model.enums.OrderOperationType;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.service.json.JsonProcessingService;
import org.hofftech.parking.service.OrderManagerService;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.service.packingalgorithm.PackingAlgorithm;
import org.hofftech.parking.parcer.ParsingService;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилитный класс для обработки файлов с информацией о посылках.
 * <p>
 * Предоставляет методы для обработки файлов, валидации данных, распределения посылок по грузовикам,
 * сохранения результатов и управления заказами.
 * </p>
 * Логирование осуществляется с помощью аннотации {@code @Slf4j}.
 * Класс использует конструктор с параметрами для внедрения зависимостей через {@code @RequiredArgsConstructor}.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class FileProcessingUtil {

    private final ParsingService parsingService;
    private final TruckService truckService;
    private final JsonProcessingService jsonProcessingService;
    private final ParcelAlgorithmFactory parcelAlgorithmFactory;
    private final OrderManagerService orderManagerService;

    /**
     * Обрабатывает файл с посылками или текстовые данные, распределяет посылки по грузовикам,
     * добавляет заказ и при необходимости сохраняет результаты в файл.
     *
     * <p>
     * В зависимости от предоставленных аргументов, метод может использовать алгоритм упрощенной или
     * четной упаковки, сохранять результаты в файл или возвращать их в виде строки.
     * </p>
     *
     * @param parcelsFile      путь к файлу с посылками
     * @param parcelsText      текстовое представление посылок
     * @param trucksFromArgs   список грузовиков, переданных через аргументы
     * @param isEasyAlgorithm  флаг использования упрощенного алгоритма
     * @param isSaveToFile       флаг сохранения результатов в файл
     * @param isEvenAlgorithm  флаг использования четного алгоритма
     * @param user             идентификатор пользователя
     * @return строковое сообщение о результате обработки
     */
    public String process(Path parcelsFile, String parcelsText, List<String> trucksFromArgs,
                          boolean isEasyAlgorithm, boolean isSaveToFile, boolean isEvenAlgorithm, String user) {
        List<Parcel> parcels = parsingService.getParcels(parcelsFile, parcelsText);
        PackingAlgorithm strategy = parcelAlgorithmFactory.createStrategy(isEasyAlgorithm);
        List<Truck> trucks = strategy.addParcels(parcels, isEasyAlgorithm, isEvenAlgorithm, trucksFromArgs);

        addLoadOrder(trucks, user);

        if (isSaveToFile) {
            saveTrucksToJson(trucks);
            return "Данные сохранены в файл.";
        } else {
            return truckService.printTrucks(trucks);
        }
    }

    /**
     * Добавляет заказ на погрузку грузовиков для указанного пользователя.
     *
     * <p>
     * Собирает все посылки из грузовиков, подсчитывает количество задействованных грузовиков и
     * создаёт новый заказ, который затем добавляется в систему управления заказами.
     * </p>
     *
     * @param trucks  список грузовиков для добавления в заказ
     * @param userId идентификатор пользователя, для которого создаётся заказ
     */
    private void addLoadOrder(List<Truck> trucks, String userId) {
        List<Parcel> allParcels = new ArrayList<>();
        int truckCount = 0;
        for (Truck truck : trucks) {
            List<Parcel> parcelsFromTruck = truck.getParcels();
            if (!parcelsFromTruck.isEmpty()) {
                allParcels.addAll(parcelsFromTruck);
                truckCount++;
            }
        }

        Order order = new Order(
                userId,
                LocalDate.now(),
                OrderOperationType.LOAD,
                truckCount,
                allParcels
        );
        orderManagerService.addOrder(order);
        log.info("Заказ на погрузку добавлен для пользователя {}", userId);
    }

    /**
     * Сохраняет информацию о грузовиках в формате JSON.
     *
     * <p>
     * Использует сервис {@link JsonProcessingService} для преобразования данных о грузовиках
     * в JSON формат и сохраняет результат.
     * </p>
     *
     * @param trucks список грузовиков для сохранения
     * @throws RuntimeException если происходит ошибка при сохранении данных в JSON
     */
    protected void saveTrucksToJson(List<Truck> trucks) {
        try {
            log.info("Сохраняем данные грузовиков в JSON...");
            String result = jsonProcessingService.saveToJson(trucks);
            log.info("Данные успешно сохранены в JSON: {}", result);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении данных в JSON: " + e.getMessage());
        }
    }
}
