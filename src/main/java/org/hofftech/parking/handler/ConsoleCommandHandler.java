package org.hofftech.parking.handler;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.service.FileProcessingService;
import org.hofftech.parking.service.JsonProcessingService;
import org.hofftech.parking.utill.FileSavingUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class ConsoleCommandHandler implements CommandHandler {
    private static final String EXIT_COMMAND = "exit";
    private static final String OUTPUT_TXT = "out/in.txt";
    private int maxTrucks;
    private String filePath;
    private String algorithm;
    private boolean useEasyAlgorithm;
    private boolean useEvenAlgorithm;
    private boolean saveToFile;

    private final FileProcessingService fileProcessingService;
    private final JsonProcessingService jsonProcessingService;

    public ConsoleCommandHandler(FileProcessingService fileProcessingService, JsonProcessingService jsonProcessingService) {
        this.fileProcessingService = fileProcessingService;
        this.jsonProcessingService = jsonProcessingService;
    }

    @Override
    public void handle(String command) {
        if (EXIT_COMMAND.equalsIgnoreCase(command)) {
            log.info("Приложение завершает работу по команде пользователя.");
            return;
        }
        if (command.startsWith("import ") || command.startsWith("save ") || command.startsWith("importjson ")) {
            parseCommand(command);
        }
        else {
            log.warn("Неизвестная команда: {}", command);
            return;
        }

        if (filePath != null) {
            try {
                log.info("Начинаем обработку файла: {}. Используем алгоритм: {}. Сохранение в файл: {}", filePath, algorithm, saveToFile);
                Path path = Path.of(filePath);
                fileProcessingService.processFile(path, useEasyAlgorithm, saveToFile, maxTrucks, useEvenAlgorithm);
            } catch (Exception e) {
                log.error("Произошла ошибка при обработке файла {}", filePath, e);
            }
        }
    }

    private void parseCommand(String command) {
        resetState();
        if (command.startsWith("importjson ")) {
            String jsonFilePath = command.replace("importjson ", "").trim();
            try {
                List<String> packageTypes = jsonProcessingService.importJson(jsonFilePath);
                FileSavingUtil.savePackagesToFile(packageTypes, OUTPUT_TXT);
            } catch (IOException e) {
                log.error("Ошибка при обработке команды importjson: {}", e.getMessage(), e);
            }
        } else {
            parseAdvancedCommand(command);
        }
    }

    private void parseAdvancedCommand(String command) {
        String toReplace = command.startsWith("import ") ? "import " : "save ";
        saveToFile = !command.startsWith("import ");
        useEasyAlgorithm = command.contains("easyalgorithm");
        algorithm = useEasyAlgorithm ? "easyalgorithm" : "multipletrucksalgorithm";
        if (command.contains("even") && !useEasyAlgorithm) {
            useEvenAlgorithm = true;
            command = command.replace("even ", "");
            algorithm = algorithm + "_even";
        }
        String[] parts = command.split(" ");
        if (!useEasyAlgorithm && parts.length > 2) {
            parseDigitInsideCommand(command, parts, toReplace);
        } else {
            if (useEvenAlgorithm) {
                log.error("Для алгоритма распределения по равным частям нужно указать кол-во грузовиков!");
                throw new RuntimeException("Не указано количество грузовиков");
            }
            filePath = command.replace(toReplace.trim(), "").trim();
        }
    }

    private void parseDigitInsideCommand(String command, String[] parts, String toReplace) {
        try {
            maxTrucks = Integer.parseInt(parts[1]); // Лимит грузовиков
            filePath = command.replace(toReplace + parts[1], "").trim();
        } catch (NumberFormatException e) {
            log.error("Некорректный формат лимита грузовиков: {}", parts[1]);
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