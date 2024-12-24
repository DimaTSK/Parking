package org.hofftech.parking.processor;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.enums.CommandConstants;
import org.hofftech.parking.service.FileProcessingService;
import org.hofftech.parking.service.JsonProcessingService;
import org.hofftech.parking.utill.FileSaving;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class ConsoleCommandProcessor implements CommandProcessor {
    private int maxTrucks;
    private String filePath;
    private String algorithm;
    private boolean useEasyAlgorithm;
    private boolean useEvenAlgorithm;
    private boolean saveToFile;

    private final FileProcessingService fileProcessingService;
    private final JsonProcessingService jsonProcessingService;

    public ConsoleCommandProcessor(FileProcessingService fileProcessingService, JsonProcessingService jsonProcessingService) {
        this.fileProcessingService = fileProcessingService;
        this.jsonProcessingService = jsonProcessingService;
    }

    @Override
    public void handle(String command) {
        if (CommandConstants.EXIT_COMMAND.getValue().equalsIgnoreCase(command)) {
            log.info("Приложение завершает работу по команде пользователя.");
            return;
        }
        if (command.startsWith("import ") || command.startsWith("save ") || command.startsWith("import_json ")) {
            parseCommand(command);
        } else {
            log.warn("Неизвестная команда: {}", command);
            return;
        }

        if (filePath != null) {
            try {
                log.info("Обработка файла: {} с использованием алгоритма: {}. Сохранение в файл: {}", filePath, algorithm, saveToFile);
                Path path = Path.of(filePath);
                fileProcessingService.processFile(path, useEasyAlgorithm, saveToFile, maxTrucks, useEvenAlgorithm);
            } catch (Exception e) {
                log.error("Ошибка при обработке файла {}: {}", filePath, e.getMessage(), e);
            }
        }
    }

    private void parseCommand(String command) {
        resetState();
        if (command.startsWith("import_json ")) {
            String jsonFilePath = command.replace("import_json ", "").trim();
            try {
                List<String> parcelsTypes = jsonProcessingService.importJson(jsonFilePath);
                FileSaving.saveParcelsToFile(parcelsTypes, CommandConstants.OUTPUT_TXT.getValue());
            } catch (IOException e) {
                log.error("Ошибка при обработке команды json: {}", e.getMessage(), e);
            }
        } else {
            parseAdvancedCommand(command);
        }
    }

    private void parseAdvancedCommand(String command) {
        String commandType = command.startsWith("import ") ? "import " : "save ";
        saveToFile = !command.startsWith("import ");
        useEasyAlgorithm = command.contains("easyAlgorithm");
        algorithm = useEasyAlgorithm ? "easyAlgorithm" : "multipleTrucksAlgorithm";

        if (command.contains("even") && !useEasyAlgorithm) {
            useEvenAlgorithm = true;
            command = command.replace("even ", "");
            algorithm += "_even";
        }

        String[] parts = command.split(" ");
        if (!useEasyAlgorithm && parts.length > 2) {
            parseDigitInsideCommand(command, parts, commandType);
        } else {
            if (useEvenAlgorithm) {
                log.error("Требуется указать количество грузовиков!");
                throw new RuntimeException("Не указано количество грузовиков");
            }
            filePath = command.replace(commandType.trim(), "").trim();
        }
    }

    private void parseDigitInsideCommand(String command, String[] parts, String commandType) {
        try {
            maxTrucks = Integer.parseInt(parts[1]);
            filePath = command.replace(commandType + parts[1], "").trim();
        } catch (NumberFormatException e) {
            log.error("Некорректный формат количества грузовиков: {}", parts[1]);
        }
    }

    private void resetState() {
        maxTrucks = Integer.MAX_VALUE;
        useEasyAlgorithm = false;
        useEvenAlgorithm = false;
        saveToFile = false;
        filePath = null;
        algorithm = null;
    }
}