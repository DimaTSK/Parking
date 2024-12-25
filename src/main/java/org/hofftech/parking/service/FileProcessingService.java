package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.FileProcessingException;
import org.hofftech.parking.exception.ParcelCreationException;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.entity.Truck;
import org.hofftech.parking.utill.ParcelParser;
import org.hofftech.parking.utill.FileReader;
import org.hofftech.parking.utill.ParcelValidator;

import java.nio.file.Path;
import java.util.List;

@Slf4j
public class FileProcessingService {
    private final FileReader fileReader;
    private final ParcelParser parcelParser;
    private final ParcelValidator parcelValidator;
    private final TruckService truckService;
    private final JsonFileService jsonFileService;

    public FileProcessingService(FileReader fileReader, ParcelParser parcelParser,
                                 ParcelValidator parcelValidator, TruckService truckService, JsonFileService jsonFileService) {
        this.fileReader = fileReader;
        this.parcelParser = parcelParser;
        this.parcelValidator = parcelValidator;
        this.truckService = truckService;
        this.jsonFileService = jsonFileService;
    }

    public void processFile(Path filePath, boolean useEasyAlgorithm, boolean isSaveToFile, int maxTrucks, boolean lazyAlg) throws ParcelCreationException {
        List<String> lines = readFile(filePath);
        validateFile(filePath, lines);
        List<ParcelDto> parcelDtos = parseFileLines(filePath, lines);
        validateParcels(parcelDtos);
        List<Truck> truckEntities = addParcels(useEasyAlgorithm, parcelDtos, maxTrucks, lazyAlg);

        if (isSaveToFile) {
            saveTrucksToFile(truckEntities);
        } else {
            truckService.printTrucks(truckEntities);
        }
    }

    private List<String> readFile(Path filePath) {
        try {
            return fileReader.readAllLines(filePath);
        } catch (Exception e) {
            log.error("Ошибка при чтении файла {}", filePath);
            throw new FileProcessingException("Ошибка чтения файла: " + filePath, e);
        }
    }

    protected void saveTrucksToFile(List<Truck> truckEntities) {
        try {
            log.info("Сохранение загруженных грузовиков в JSON");
            jsonFileService.saveTrucksToJson(truckEntities);  // Использование метода saveTrucksToJson
        } catch (Exception e) {
            log.error("Ошибка при сохранении в JSON", e);
            throw new RuntimeException("Ошибка при сохранении в JSON", e);
        }
    }

    public List<Truck> addParcels(boolean useEasyAlgorithm, List<ParcelDto> parcelDtos, int maxTrucks, Boolean lazyAlg) {
        List<Truck> truckEntities;
        if (useEasyAlgorithm) {
            truckEntities = truckService.addParcelsToIndividualTrucks(parcelDtos);
        } else {
            truckEntities = truckService.addParcelsToMultipleTrucks(parcelDtos, maxTrucks, lazyAlg);
        }
        return truckEntities;
    }

    private void validateParcels(List<ParcelDto> parcelDtos) {
        if (!parcelValidator.isValidParcels(parcelDtos)) {
            log.warn("Некоторые упаковки не прошли валидацию.");
        } else {
            log.info("Все упаковки успешно прошли валидацию.");
        }
    }

    protected List<ParcelDto> parseFileLines(Path filePath, List<String> lines) throws ParcelCreationException {
        List<ParcelDto> parcelDtos = parcelParser.parseParcels(lines);
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
