package org.hofftech.parking.processor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.ParcelCreationException;
import org.hofftech.parking.model.enums.CommandConstants;
import org.hofftech.parking.service.FileProcessingService;
import org.hofftech.parking.service.JsonFileService;
import org.hofftech.parking.util.FileWriter;

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
        } catch (ParcelCreationException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleImportJsonCommand(String command) {
        String jsonFilePath = command.replace("import_json ", "").trim();
        try {
            List<String> parcelsTypes = jsonFileService.importJson(jsonFilePath);
            FileWriter.saveParcelsToFile(parcelsTypes, CommandConstants.OUTPUT_TXT.getValue());
        } catch (IOException e) {
            log.error("Ошибка при обработке команды json: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Ошибка обработки JSON файла");
        }
    }

    public void processGeneralCommand(String command) throws ParcelCreationException {
        CommandParser params = parseCommandFlags(command);
        parseCommandArguments(command, params);
        validateParameters(params);
        fileProcessingService.processFile(
                Path.of(params.getFilePath()),
                params.isUseEasyAlgorithm(),
                params.isSaveToFile(),
                params.getMaxTrucks(),
                params.isUseEvenAlgorithm()
        );
    }

    private CommandParser parseCommandFlags(String command) {
        boolean saveToFile = command.startsWith("save ");
        boolean useEasyAlgorithm = command.contains("easyAlgorithm");
        boolean useEvenAlgorithm = command.contains("even") && !useEasyAlgorithm;

        String algorithm = useEasyAlgorithm ? "easyAlgorithm" : "multipleTrucksAlgorithm";
        if (useEvenAlgorithm) {
            algorithm += "_even";
            command = command.replace("even ", "");
        }

        return new CommandParser(saveToFile, useEasyAlgorithm, useEvenAlgorithm, algorithm, command);
    }

    private void parseCommandArguments(String command, CommandParser params) {
        String[] parts = params.getCommand().split(" ");
        int maxTrucks = Integer.MAX_VALUE;
        List<String> filePathParts = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (params.isUseEvenAlgorithm() && part.equalsIgnoreCase("even")) {
                params.setUseEvenAlgorithm(true);
                params.setAlgorithm("multipleTrucksAlgorithm_even");
            } else {
                try {
                    int trucks = Integer.parseInt(part);
                    maxTrucks = trucks;
                } catch (NumberFormatException ex) {
                    filePathParts.add(part);
                }
            }
        }

        String filePath = String.join(" ", filePathParts).trim();
        params.setMaxTrucks(maxTrucks);
        params.setFilePath(filePath);
    }

    private void validateParameters(CommandParser params) {
        if (params.isUseEvenAlgorithm() && params.getMaxTrucks() == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Не указано количество грузовиков");
        }

        if (params.getFilePath().isEmpty()) {
            throw new IllegalArgumentException("Не указан путь к файлу");
        }
    }

    @Getter
    @Setter
    private static class CommandParser {
        private boolean saveToFile;
        private boolean useEasyAlgorithm;
        private boolean useEvenAlgorithm;
        private String algorithm;
        private String command;
        private int maxTrucks;
        private String filePath;

        public CommandParser(boolean saveToFile, boolean useEasyAlgorithm, boolean useEvenAlgorithm, String algorithm, String command) {
            this.saveToFile = saveToFile;
            this.useEasyAlgorithm = useEasyAlgorithm;
            this.useEvenAlgorithm = useEvenAlgorithm;
            this.algorithm = algorithm;
            this.command = command;
        }
    }
}