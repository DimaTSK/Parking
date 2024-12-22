package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Package;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.utill.FileParserUtil;
import org.hofftech.parking.utill.FileReaderUtil;

import java.nio.file.Path;
import java.util.List;

@Slf4j
public class FileProcessingService {
    private final FileReaderUtil fileReader;
    private final FileParserUtil fileParser;
    private final ValidatorService validatorService;
    private final TruckService truckService;
    private final JsonProcessingService jsonProcessingService;

    public FileProcessingService(FileReaderUtil fileReader, FileParserUtil fileParser,
                                 ValidatorService validatorService, TruckService truckService, JsonProcessingService jsonProcessingService) {
        this.fileReader = fileReader;
        this.fileParser = fileParser;
        this.validatorService = validatorService;
        this.truckService = truckService;
        this.jsonProcessingService = jsonProcessingService;
    }

    public void processFile(Path filePath, boolean useEasyAlgorithm, boolean saveToFile, int maxTrucks, boolean lazyAlg) {
        List<String> lines = readFile(filePath);
        validateFile(filePath, lines);
        List<Package> packages = parseFileLines(filePath, lines);
        validatePackages(packages);
        List<Truck> trucks = addPackages(useEasyAlgorithm, packages, maxTrucks, lazyAlg);

        if (saveToFile) {
            saveTrucksToFile(trucks);
        } else {
            printTrucks(trucks);
        }
    }

    private List<String> readFile(Path filePath) {
        try {
            return fileReader.readAllLines(filePath);
        } catch (Exception e) {
            log.error("Произошла ошибка чтения файла {}", filePath);
            throw new RuntimeException("Ошибка чтения файла: " + filePath, e);
        }
    }

    protected void saveTrucksToFile(List<Truck> trucks) {
        try {
            log.info("Сохраняем данные грузовиков в JSON...");
            jsonProcessingService.saveToJson(trucks);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения грузовиков в JSON", e);
        }
    }

    private void printTrucks(List<Truck> trucks) {
        truckService.printTrucks(trucks);
    }


    protected List<Truck> addPackages(boolean useEasyAlgorithm, List<Package> packages, int maxTrucks, Boolean lazyAlg) {
        // Распределение упаковок по грузовикам
        List<Truck> trucks;
        if (useEasyAlgorithm) {
            trucks = truckService.addPackagesToIndividualTrucks(packages);
        } else {
            trucks = truckService.addPackagesToMultipleTrucks(packages, maxTrucks, lazyAlg);
        }
        return trucks;
    }

    private void validatePackages(List<Package> packages) {
        // Валидация упаковок
        if (!validatorService.isValidPackages(packages)) {
            log.warn("Не все упаковки прошли валидацию.");
        }
        log.info("Все упаковки успешно прошли валидацию.");
    }

    protected List<Package> parseFileLines(Path filePath, List<String> lines) {
        // Парсинг строк в упаковки
        List<Package> packages = fileParser.parsePackages(lines);
        if (packages.isEmpty()) {
            log.warn("Не удалось распарсить ни одной упаковки из файла: {}", filePath);
        }
        log.info("Успешно распарсено {} упаковок.", packages.size());
        return packages;
    }

    protected void validateFile(Path filePath, List<String> lines) {
        // Валидация файла
        if (!validatorService.isValidFile(lines)) {
            log.error("Файл не прошел валидацию");
            throw new RuntimeException("Файл не прошел валидацию: " + filePath);
        }
        log.info("Файл успешно прошел валидацию.");
    }
}