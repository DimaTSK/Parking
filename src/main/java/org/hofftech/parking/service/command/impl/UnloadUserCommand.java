package org.hofftech.parking.service.command.impl;


import lombok.RequiredArgsConstructor;
import org.hofftech.parking.exception.FileSavingException;
import org.hofftech.parking.exception.OutputFileException;
import org.hofftech.parking.exception.UserNotProvidedException;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.service.FileSavingService;
import org.hofftech.parking.service.json.JsonProcessingService;
import org.hofftech.parking.service.command.UserCommand;


import java.util.List;
import java.util.Map;
/**
 * Класс реализации пользовательской команды для создания посылки.
 */
@RequiredArgsConstructor
public class UnloadUserCommand implements UserCommand {

    private final JsonProcessingService jsonProcessingService;
    private final FileSavingService fileSavingService;

    private static final String OUTPUT_FILE_PATH = "out/in.txt";
    /**
     * Выполняет команду создания посылки на основе переданной команды.
     */
    @Override
    public String execute(ParsedCommand command) {
        String inFile = command.getInFile();
        boolean isWithCount = command.isWithCount();
        String user = command.getUser();

        if (inFile == null || inFile.isEmpty()) {
            throw new OutputFileException("Путь к JSON-файлу не указан");
        }

        if (user == null || user.isEmpty()) {
            throw new UserNotProvidedException("Пользователь должен быть передан для команды UNLOAD");
        }

        try {
            List<Map<String, Long>> parcelsCountMap = jsonProcessingService.importParcelsFromJson(inFile,
                    isWithCount, user);
            fileSavingService.saveParcels(parcelsCountMap, OUTPUT_FILE_PATH, isWithCount);
            return "Файл успешно импортирован из JSON: " + inFile;
        } catch (FileSavingException e) {
            throw new FileSavingException("Ошибка при сохранении файла: " + e.getMessage(), e);
        }
    }
}