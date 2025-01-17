package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.CommandProcessingException;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.service.FileProcessingService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик команды загрузки посылок. Реализует интерфейс {@link CommandProcessor}.
 *
 * <p>Этот класс отвечает за обработку команды загрузки посылок из текстового ввода или файла.
 * Он использует {@link FileProcessingService} для обработки данных о посылках и грузовиках,
 * а также управляет выбором алгоритмов обработки и сохранением результатов.</p>
 *
 * <p>Возможные источники посылок:
 * <ul>
 *     <li>Текстовый ввод {@code parcelsText}</li>
 *     <li>Файл {@code parcelsFile}</li>
 * </ul>
 * </p>
 */
@RequiredArgsConstructor
@Slf4j
public class LoadCommandProcessor implements CommandProcessor {
    private final FileProcessingService fileProcessingService;

    /**
     * Выполняет обработку команды загрузки посылок.
     *
     * <p>Метод выполняет следующие шаги:
     * <ul>
     *     <li>Извлекает параметры команды из объекта {@link ParsedCommand}.</li>
     *     <li>Разбирает список грузовиков из текстового ввода, если он предоставлен.</li>
     *     <li>Если предоставлен текстовый ввод посылок, вызывает {@link FileProcessingService#processFile} с текстом.</li>
     *     <li>Если предоставлен файл с посылками, вызывает {@link FileProcessingService#processFile} с путем к файлу.</li>
     *     <li>Логирует успешную обработку посылок.</li>
     *     <li>Если ни текстовый ввод, ни файл не предоставлены, выбрасывает {@link CommandProcessingException}.</li>
     * </ul>
     * </p>
     *
     * @param command объект {@link ParsedCommand}, содержащий данные для выполнения команды загрузки
     * @throws CommandProcessingException если ни текстовый ввод, ни файл с посылками не указаны
     */
    @Override
    public void execute(ParsedCommand command) {
        String parcelsText = command.getParcelsText();
        String parcelsFile = command.getParcelsFile();
        String trucksText = command.getTrucks();
        boolean useEasyAlgorithm = command.isUseEasyAlgorithm();
        boolean useEvenAlgorithm = command.isUseEvenAlgorithm();
        boolean saveToFile = command.isSaveToFile();

        List<String> trucksFromArgs = trucksText != null && !trucksText.isEmpty()
                ? new ArrayList<>(List.of(trucksText.split(",")))
                : new ArrayList<>();

        if (parcelsText != null && !parcelsText.isEmpty()) {
            fileProcessingService.processFile(null, parcelsText, trucksFromArgs, useEasyAlgorithm, saveToFile, useEvenAlgorithm);
            log.info("Посылки успешно обработаны из текстового ввода.");
        } else if (parcelsFile != null && !parcelsFile.isBlank()) {
            fileProcessingService.processFile(Path.of(parcelsFile), null, trucksFromArgs, useEasyAlgorithm, saveToFile, useEvenAlgorithm);
            log.info("Файл успешно импортирован из JSON: {}", parcelsFile);
        } else {
            String errorMsg = "Ошибка: Укажите источник посылок (текст или файл)";
            log.error(errorMsg);
            throw new CommandProcessingException(errorMsg);
        }
    }
}
