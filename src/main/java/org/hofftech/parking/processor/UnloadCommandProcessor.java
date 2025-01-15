package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.service.JsonProcessingService;
import org.hofftech.parking.util.FileSavingUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class UnloadCommandProcessor implements CommandProcessor {
    private final JsonProcessingService jsonProcessingService;
    private static final String OUTPUT_FILE_PATH = "out/in.txt";

    @Override
    public void execute(ParsedCommand command) {
        String inFile = command.getInFile();
        boolean withCount = command.isWithCount();

        if (inFile == null || inFile.isEmpty()) {
            log.error("Путь к JSON-файлу не указан.");
            throw new IllegalArgumentException("Путь к JSON-файлу не указан.");
        }
        try {
            log.info("Импортируем данные из JSON");
            List<Map.Entry<String, Long>> packageNamesAndCount = jsonProcessingService.importPackagesFromJson(inFile, withCount);

            log.info("Сохраняем данные в файл");
            FileSavingUtil.savePackagesToFile(packageNamesAndCount, OUTPUT_FILE_PATH, withCount);

            log.info("Файл успешно импортирован из JSON: {}", inFile);
        } catch (IOException e) {
            log.error("Ошибка при обработке команды importJson: {}", e.getMessage());
            throw new RuntimeException("Не удалось обработать команду importJson", e);
        }
    }
}
