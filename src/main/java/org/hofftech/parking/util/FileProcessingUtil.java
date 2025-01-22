package org.hofftech.parking.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.factory.ParcelAlgorithmFactory;
import org.hofftech.parking.model.Order;
import org.hofftech.parking.model.enums.OrderOperationType;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.parcer.ParsingService;
import org.hofftech.parking.service.json.JsonProcessingService;
import org.hofftech.parking.service.OrderManagerService;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.service.packingalgorithm.PackingAlgorithm;
import org.hofftech.parking.validator.ParcelValidator;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class FileProcessingUtil {

    private final ParsingService fileParser;
    private final ParcelValidator parcelValidator;
    private final TruckService truckService;
    private final JsonProcessingService jsonProcessingService;
    private final ParcelAlgorithmFactory parcelAlgorithmFactory;
    private final OrderManagerService orderManagerService;

    public String processFile(Path parcelsFile, String parcelsText, List<String> trucksFromArgs,
                              boolean isEasyAlgorithm, boolean saveToFile, boolean isEvenAlgorithm, String user) {
        List<Parcel> parcels = getParcelsFromFileOrArgs(parcelsFile, parcelsText);
        PackingAlgorithm strategy = parcelAlgorithmFactory.createStrategy(isEasyAlgorithm);
        List<Truck> trucks = strategy.addParcels(parcels, isEasyAlgorithm, isEvenAlgorithm, trucksFromArgs);

        addLoadOrder(trucks, user);

        if (saveToFile) {
            saveTrucksToJson(trucks);
            return "Данные успешно сохранены в файл.";
        } else {
            return truckService.printTrucks(trucks);
        }
    }

    private void addLoadOrder(List<Truck> trucks, String userId) {
        List<Parcel> allParcels = new ArrayList<>();
        int truckCount = 0;
        for (Truck truck : trucks) {
            List<Parcel> parcelsFromTruck = truck.getParcels();
            if (!parcelsFromTruck.isEmpty()) {
                allParcels.addAll(truck.getParcels());
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

    private List<Parcel> getParcelsFromFileOrArgs(Path parcelsFile, String parcelsText) {
        List<Parcel> parcels = new ArrayList<>();
        if (parcelsFile != null) {
            List<String> lines = FileReaderUtil.readAllLines(parcelsFile);
            parcelValidator.validateFile(lines);
            parcels = parseFileLines(parcelsFile, lines);
        } else if (parcelsText != null && !parcelsText.isEmpty()) {
            parcels = fileParser.getParcelFromArgs(parcelsText);
        }
        if (parcels.isEmpty()) {
            throw new IllegalArgumentException("Упаковки не представлены, продолжение работы невозможно");
        }
        return parcels;
    }

    protected void saveTrucksToJson(List<Truck> trucks) {
        try {
            log.info("Сохраняем данные грузовиков в JSON...");
            String result = jsonProcessingService.saveToJson(trucks);
            log.info("Данные успешно сохранены в JSON: {}", result);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении данных в JSON: " + e.getMessage());
        }
    }

    protected List<Parcel> parseFileLines(Path filePath, List<String> lines) {
        List<Parcel> parcels = fileParser.parseParcelsFromFile(lines);
        if (parcels.isEmpty()) {
            throw new RuntimeException("Не удалось распарсить ни одной упаковки из файла: " + filePath);
        }
        log.info("Успешно распарсено {} упаковок.", parcels.size());
        return parcels;
    }
}