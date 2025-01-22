package org.hofftech.parking.service.command.impl;


import lombok.RequiredArgsConstructor;
import org.hofftech.parking.exception.OutputFileException;
import org.hofftech.parking.exception.UserNotProvidedException;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.util.FileSavingUtil;
import org.hofftech.parking.service.json.JsonProcessingService;
import org.hofftech.parking.service.command.UserCommand;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class UnloadUserCommand implements UserCommand {

    private final JsonProcessingService jsonProcessingService;
    private final FileSavingUtil fileSavingUtil;

    private static final String OUTPUT_FILE_PATH = "out/in.txt";

    @Override
    public String execute(ParsedCommand command) {
        String inFile = command.getInFile();
        boolean isWithCount = command.isIswithCount();
        String user = command.getUser();

        if (inFile == null || inFile.isEmpty()) {
            throw new OutputFileException("Путь к JSON-файлу не указан");
        }

        if (user == null || user.isEmpty()) {
            throw new UserNotProvidedException("Пользователь должен быть передан для комынды UNLOAD");
        }

        try {
            List<Map<String, Long>> parcelsCountMap = jsonProcessingService.importParcelsFromJson(inFile,
                    isWithCount, user);
            fileSavingUtil.saveParcelsToFile(parcelsCountMap, OUTPUT_FILE_PATH, isWithCount);
            return "Файл успешно импортирован из JSON: " + inFile;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при обработке команды importJson: " + e.getMessage());
        }
    }
}