package org.hofftech.parking.processor;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.enums.CommandConstants;
import org.hofftech.parking.service.FileProcessingService;
import org.hofftech.parking.service.JsonFileService;
import org.hofftech.parking.util.FileSaving;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConsoleCommandProcessor implements CommandProcessor {
    private final FileProcessingService fileProcessingService;
    private final JsonFileService jsonFileService;

    public ConsoleCommandProcessor(FileProcessingService fileProcessingService, JsonFileService jsonFileService) {
        this.fileProcessingService = fileProcessingService;
        this.jsonFileService = jsonFileService;
    }

    @Override
    public void handle(String command) {
        if (CommandConstants.EXIT_COMMAND.getValue().equalsIgnoreCase(command)) {
            log.info("Приложение завершает работу по команде пользователя.");
            return;
        }

        try {
            if (command.startsWith("import_json ")) {
                handleImportJsonCommand(command);
            } else {
                processGeneralCommand(command);
            }
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
        }
    }

    private void handleImportJsonCommand(String command) {
        String jsonFilePath = command.replace("import_json ", "").trim();
        try {
            List<String> parcelsTypes = jsonFileService.importJson(jsonFilePath);
            FileSaving.saveParcelsToFile(parcelsTypes, CommandConstants.OUTPUT_TXT.getValue());
        } catch (IOException e) {
            log.error("Ошибка при обработке команды json: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Ошибка обработки JSON файла");
        }
    }

    private void processGeneralCommand(String command) {
        boolean saveToFile = command.startsWith("save ");
        boolean useEasyAlgorithm = command.contains("easyAlgorithm");
        boolean useEvenAlgorithm = command.contains("even") && !useEasyAlgorithm;

        String algorithm = useEasyAlgorithm ? "easyAlgorithm" : "multipleTrucksAlgorithm";
        if (useEvenAlgorithm) {
            algorithm += "_even";
            command = command.replace("even ", "");
        }

        String[] parts = command.split(" ");
        int maxTrucks = Integer.MAX_VALUE;
        String filePath = "";

        List<String> filePathParts = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (useEvenAlgorithm && part.equalsIgnoreCase("even")) {
                useEvenAlgorithm = true;
                algorithm = "multipleTrucksAlgorithm_even";
            } else {
                try {
                    int trucks = Integer.parseInt(part);
                    maxTrucks = trucks;
                } catch (NumberFormatException ex) {
                    filePathParts.add(part);
                }
            }
        }

        filePath = String.join(" ", filePathParts).trim();

        if (useEvenAlgorithm && maxTrucks == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Не указано количество грузовиков");
        }

        if (filePath.isEmpty()) {
            throw new IllegalArgumentException("Не указан путь к файлу");
        }

        processFile(filePath, useEasyAlgorithm, saveToFile, maxTrucks, useEvenAlgorithm, algorithm);
    }

    private void processFile(String filePath, boolean useEasyAlgorithm, boolean saveToFile, int maxTrucks, boolean useEvenAlgorithm, String algorithm) {
        try {
            log.info("Обработка файла: {} с использованием алгоритма: {}. Сохранение в файл: {}",
                    filePath, algorithm, saveToFile);
            Path path = Path.of(filePath);
            fileProcessingService.processFile(path, useEasyAlgorithm, saveToFile, maxTrucks, useEvenAlgorithm);
        } catch (Exception e) {
            log.error("Ошибка при обработке файла {}: {}", filePath, e.getMessage(), e);
        }
    }
}