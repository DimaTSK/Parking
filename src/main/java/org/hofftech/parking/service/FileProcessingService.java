package org.hofftech.parking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.service.strategy.PackingStrategy;
import org.hofftech.parking.factory.PackingStrategyFactory;
import org.hofftech.parking.validator.ParcelValidator;
import org.hofftech.parking.util.FileReaderUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для обработки файлов, содержащих информацию об упаковках и грузовиках.
 * Отвечает за чтение, валидацию, парсинг данных и сохранение результатов в JSON файл или вывод на экран.
 */
@Slf4j
@RequiredArgsConstructor
public class FileProcessingService {
    private final ParsingService fileParser;
    private final ParcelValidator parcelValidator;
    private final TruckService truckService;
    private final JsonProcessingService jsonProcessingService;
    private final PackingStrategyFactory packingStrategyFactory;

    /**
     * Обрабатывает файл с данными об упаковках или текстовые данные, распределяет упаковки по грузовикам
     * с использованием выбранной стратегии упаковки и сохраняет результаты или выводит их на экран.
     *
     * @param parcelsFile   Путь к файлу с данными об упаковках. Может быть {@code null}, если используются текстовые данные.
     * @param parcelsText   Текстовые данные об упаковках. Может быть {@code null} или пустым, если используется файл.
     * @param trucksFromArgs Список грузовиков, переданных через аргументы.
     * @param useEasyAlg    Флаг, указывающий, использовать ли простую стратегию упаковки.
     * @param saveToFile    Флаг, указывающий, сохранять результаты в JSON файл или выводить на экран.
     * @param useEvenAlg    Флаг, указывающий, использовать ли алгоритм для равномерного распределения.
     */
    public void processFile(Path parcelsFile, String parcelsText, List<String> trucksFromArgs,
                            boolean useEasyAlg,
                            boolean saveToFile,
                            boolean useEvenAlg) {
        List<Parcel> parcels = getPackagesFromFileOrArgs(parcelsFile, parcelsText);
        PackingStrategy strategy = packingStrategyFactory.getStrategy(useEasyAlg);
        List<Truck> trucks = strategy.addPackages(parcels, useEasyAlg, useEvenAlg, trucksFromArgs);

        if (saveToFile) {
            saveTrucksToJson(trucks);
        } else {
            truckService.printTrucks(trucks);
        }
    }

    /**
     * Получает список упаковок из файла или из переданных текстовых данных.
     *
     * @param parcelsFile Путь к файлу с данными об упаковках.
     * @param parcelsText Текстовые данные об упаковках.
     * @return Список объектов {@link Parcel}, представляющих упаковки.
     * @throws IllegalArgumentException если список упаковок пуст после обработки файла или текста.
     */
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

    /**
     * Сохраняет информацию о грузовиках в JSON файл.
     *
     * @param trucks Список грузовиков для сохранения.
     * @throws RuntimeException если возникает ошибка при сохранении данных в JSON.
     */
    private void saveTrucksToJson(List<Truck> trucks) {
        try {
            log.info("Сохраняем данные грузовиков в JSON...");
            String result = jsonProcessingService.saveToJson(trucks);
            log.info("Данные успешно сохранены в JSON: {}", result);
        } catch (Exception e) {
            log.error("Ошибка при сохранении данных в JSON: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Парсит строки из файла и преобразует их в список объектов {@link Parcel}.
     *
     * @param filePath Путь к файлу с данными.
     * @param lines    Список строк из файла.
     * @return Список объектов {@link Parcel}, представляющих упаковки.
     */
    protected List<Parcel> parseFileLines(Path filePath, List<String> lines) {
        List<Parcel> parcels = fileParser.parsePackagesFromFile(lines);
        if (parcels.isEmpty()) {
            log.warn("Не удалось распарсить ни одной упаковки из файла: {}", filePath);
        }
        log.info("Успешно распарсено {} упаковок.", parcels.size());
        return parcels;
    }

    /**
     * Валидирует содержимое файла с данными об упаковках.
     *
     * @param filePath Путь к файлу, который проверяется.
     * @param lines    Список строк из файла для валидации.
     * @throws RuntimeException если файл не прошел валидацию.
     */
    protected void validateFile(Path filePath, List<String> lines) {
        if (!parcelValidator.isValidFile(lines)) {
            log.error("Файл не прошел валидацию");
            throw new RuntimeException("Файл не прошел валидацию: " + filePath);
        }
        log.info("Файл успешно прошел валидацию.");
    }
}
