package org.hofftech.parking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.service.strategy.DefaultPackingStrategy;
import org.hofftech.parking.service.strategy.PackingStrategyFactory;
import org.hofftech.parking.validator.ParcelValidator;
import org.hofftech.parking.util.FileReaderUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class FileProcessingService {
    private final ParsingService fileParser;
    private final ParcelValidator parcelValidator;
    private final TruckService truckService;
    private final JsonProcessingService jsonProcessingService;
    private final PackingStrategyFactory packingStrategyFactory;

    public void processFile(Path parcelsFile, String parcelsText, List<String> trucksFromArgs,
                            boolean useEasyAlg,
                            boolean saveToFile,
                            boolean useEvenAlg) {
        List<Parcel> parcels = getPackagesFromFileOrArgs(parcelsFile, parcelsText);
        DefaultPackingStrategy strategy = packingStrategyFactory.getStrategy(useEasyAlg);
        List<Truck> trucks = strategy.addPackages(parcels, useEasyAlg, useEvenAlg, trucksFromArgs);

        if (saveToFile) {
            saveTrucksToJson(trucks);
        } else {
            truckService.printTrucks(trucks);
        }
    }

    private List<Parcel> getPackagesFromFileOrArgs(Path parcelsFile, String parcelsText) {
        List<Parcel> parcels = new ArrayList<>();
        if (parcelsFile != null) {
            List<String> lines = FileReaderUtil.readAllLines(parcelsFile);
            validateFile(parcelsFile, lines);
            parcels = parseFileLines(parcelsFile, lines);
        } else if (parcelsText != null && !parcelsText.isEmpty()) {
            parcels = fileParser.getPackagesFromArgs(parcelsText);
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
            log.error("Ошибка при сохранении данных в JSON: {}", e.getMessage(), e);
            throw e;
        }
    }

    protected List<Parcel> parseFileLines(Path filePath, List<String> lines) {
        List<Parcel> parcels = fileParser.parsePackagesFromFile(lines);
        if (parcels.isEmpty()) {
            log.warn("Не удалось распарсить ни одной упаковки из файла: {}", filePath);
        }
        log.info("Успешно распарсено {} упаковок.", parcels.size());
        return parcels;
    }

    protected void validateFile(Path filePath, List<String> lines) {
        if (!parcelValidator.isValidFile(lines)) {
            log.error("Файл не прошел валидацию");
            throw new RuntimeException("Файл не прошел валидацию: " + filePath);
        }
        log.info("Файл успешно прошел валидацию.");
    }
}