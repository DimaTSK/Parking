package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.TruckDto;
import org.hofftech.parking.utill.FileParser;
import org.hofftech.parking.utill.FileReader;

import java.nio.file.Path;
import java.util.List;

@Slf4j
public class FileProcessingService {
    private final FileReader fileReader;
    private final FileParser fileParser;
    private final ValidatorService validatorService;
    private final TruckService truckService;
    private final JsonProcessingService jsonProcessingService;

    public FileProcessingService(FileReader fileReader, FileParser fileParser,
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
        List<ParcelDto> parcelDtos = parseFileLines(filePath, lines);
        validatePackages(parcelDtos);
        List<TruckDto> truckDtos = addPackages(useEasyAlgorithm, parcelDtos, maxTrucks, lazyAlg);

        if (saveToFile) {
            saveTrucksToFile(truckDtos);
        } else {
            printTrucks(truckDtos);
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

    protected void saveTrucksToFile(List<TruckDto> truckDtos) {
        try {
            log.info("Сохраняем данные грузовиков в JSON...");
            jsonProcessingService.saveToJson(truckDtos);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения грузовиков в JSON", e);
        }
    }

    private void printTrucks(List<TruckDto> truckDtos) {
        truckService.printTrucks(truckDtos);
    }


    protected List<TruckDto> addPackages(boolean useEasyAlgorithm, List<ParcelDto> parcelDtos, int maxTrucks, Boolean lazyAlg) {
        // Распределение упаковок по грузовикам
        List<TruckDto> truckDtos;
        if (useEasyAlgorithm) {
            truckDtos = truckService.addPackagesToIndividualTrucks(parcelDtos);
        } else {
            truckDtos = truckService.addPackagesToMultipleTrucks(parcelDtos, maxTrucks, lazyAlg);
        }
        return truckDtos;
    }

    private void validatePackages(List<ParcelDto> parcelDtos) {
        // Валидация упаковок
        if (!validatorService.isValidPackages(parcelDtos)) {
            log.warn("Не все упаковки прошли валидацию.");
        }
        log.info("Все упаковки успешно прошли валидацию.");
    }

    protected List<ParcelDto> parseFileLines(Path filePath, List<String> lines) {
        // Парсинг строк в упаковки
        List<ParcelDto> parcelDtos = fileParser.parsePackages(lines);
        if (parcelDtos.isEmpty()) {
            log.warn("Не удалось распарсить ни одной упаковки из файла: {}", filePath);
        }
        log.info("Успешно распарсено {} упаковок.", parcelDtos.size());
        return parcelDtos;
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