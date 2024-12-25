package org.hofftech.parking.processor;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.CommandContext;
import org.hofftech.parking.model.enums.CommandConstants;
import org.hofftech.parking.service.FileProcessingService;
import org.hofftech.parking.service.JsonFileService;
import org.hofftech.parking.util.FileSaving;

import java.io.IOException;
import java.nio.file.Path;
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
            CommandContext context = parseCommand(command);
            processFile(context);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
        }
    }

    private CommandContext parseCommand(String command) {
        CommandContext context = new CommandContext();

        if (command.startsWith("import_json ")) {
            String jsonFilePath = command.replace("import_json ", "").trim();
            try {
                List<String> parcelsTypes = jsonFileService.importJson(jsonFilePath);
                FileSaving.saveParcelsToFile(parcelsTypes, CommandConstants.OUTPUT_TXT.getValue());
            } catch (IOException e) {
                log.error("Ошибка при обработке команды json: {}", e.getMessage(), e);
                throw new IllegalArgumentException("Ошибка обработки JSON файла");
            }
        } else {
            parseAdvancedCommand(command, context);
        }

        return context;
    }

    private void parseAdvancedCommand(String command, CommandContext context) {
        String commandType = command.startsWith("import ") ? "import " : "save ";
        context.setSaveToFile(!command.startsWith("import "));
        context.setUseEasyAlgorithm(command.contains("easyAlgorithm"));
        context.setAlgorithm(context.isUseEasyAlgorithm() ? "easyAlgorithm" : "multipleTrucksAlgorithm");

        if (command.contains("even") && !context.isUseEasyAlgorithm()) {
            context.setUseEvenAlgorithm(true);
            command = command.replace("even ", "");
            context.setAlgorithm(context.getAlgorithm() + "_even");
        }

        String[] parts = command.split(" ");
        if (!context.isUseEasyAlgorithm() && parts.length > 2) {
            parseDigitInsideCommand(command, parts, context, commandType);
        } else {
            if (context.isUseEvenAlgorithm()) {
                throw new IllegalArgumentException("Не указано количество грузовиков");
            }
            context.setFilePath(command.replace(commandType.trim(), "").trim());
        }
    }

    private void parseDigitInsideCommand(String command, String[] parts, CommandContext context, String commandType) {
        try {
            context.setMaxTrucks(Integer.parseInt(parts[1]));
            context.setFilePath(command.replace(commandType + parts[1], "").trim());
        } catch (NumberFormatException e) {
            log.error("Некорректный формат количества грузовиков: {}", parts[1]);
            throw new IllegalArgumentException("Некорректный формат количества грузовиков");
        }
    }

    private void processFile(CommandContext context) {
        if (context.getFilePath() != null) {
            try {
                log.info("Обработка файла: {} с использованием алгоритма: {}. Сохранение в файл: {}",
                        context.getFilePath(), context.getAlgorithm(), context.isSaveToFile());
                Path path = Path.of(context.getFilePath());
                fileProcessingService.processFile(path, context.isUseEasyAlgorithm(), context.isSaveToFile(),
                        context.getMaxTrucks(), context.isUseEvenAlgorithm());
            } catch (Exception e) {
                log.error("Ошибка при обработке файла {}: {}", context.getFilePath(), e.getMessage(), e);
            }
        }
    }
}