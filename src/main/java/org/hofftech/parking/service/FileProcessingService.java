package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.TruckDto;
import org.hofftech.parking.utill.FileParser;
import org.hofftech.parking.utill.FileReader;
import org.hofftech.parking.utill.ParcelValidator;

import java.nio.file.Path;
import java.util.List;

@Slf4j
public class FileProcessingService {
    private final FileReader fileReader;
    private final FileParser fileParser;
    private final ParcelValidator parcelValidator;
    private final TruckService truckService;
    private final JsonProcessingService jsonProcessingService;

    public FileProcessingService(FileReader fileReader, FileParser fileParser,
                                 ParcelValidator parcelValidator, TruckService truckService, JsonProcessingService jsonProcessingService) {
        this.fileReader = fileReader;
        this.fileParser = fileParser;
        this.parcelValidator = parcelValidator;
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
            log.error("Ошибка при чтении файла {}", filePath);
            throw new RuntimeException("Ошибка чтения файла: " + filePath, e);
        }
    }

    protected void saveTrucksToFile(List<TruckDto> truckDtos) {
        try {
            log.info("Сохранение загруженных грузовиков в JSON");
            jsonProcessingService.saveToJson(truckDtos);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении в JSON", e);
        }
    }

    private void printTrucks(List<TruckDto> truckDtos) {
        truckService.printTrucks(truckDtos);
    }

    protected List<TruckDto> addPackages(boolean useEasyAlgorithm, List<ParcelDto> parcelDtos, int maxTrucks, Boolean lazyAlg) {
        List<TruckDto> truckDtos;
        if (useEasyAlgorithm) {
            truckDtos = truckService.addPackagesToIndividualTrucks(parcelDtos);
        } else {
            truckDtos = truckService.addPackagesToMultipleTrucks(parcelDtos, maxTrucks, lazyAlg);
        }
        return truckDtos;
    }

    private void validatePackages(List<ParcelDto> parcelDtos) {
        if (!parcelValidator.isValidPackages(parcelDtos)) {
            log.warn("Некоторые упаковки не прошли валидацию.");
        } else {
            log.info("Все упаковки успешно прошли валидацию.");
        }
    }

    protected List<ParcelDto> parseFileLines(Path filePath, List<String> lines) {
        List<ParcelDto> parcelDtos = fileParser.parsePackages(lines);
        if (parcelDtos.isEmpty()) {
            log.warn("Не удалось распарсить ни одной упаковки из файла: {}", filePath);
        } else {
            log.info("Успешно распарсено {} упаковок.", parcelDtos.size());
        }
        return parcelDtos;
    }

    protected void validateFile(Path filePath, List<String> lines) {
        if (!parcelValidator.isValidFile(lines)) {
            log.error("Файл не прошел валидацию: {}", filePath);
            throw new RuntimeException("Файл не прошел валидацию: " + filePath);
        }
        log.info("Файл успешно прошел валидацию.");
    }
}