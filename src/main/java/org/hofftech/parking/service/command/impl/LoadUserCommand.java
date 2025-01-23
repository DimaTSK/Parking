package org.hofftech.parking.service.command.impl;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.exception.ParcelLoadingException;
import org.hofftech.parking.exception.UserNotProvidedException;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.model.enums.ParcelSourceType;
import org.hofftech.parking.util.FileProcessingUtil;
import org.hofftech.parking.service.command.UserCommand;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Класс реализации пользовательской команды для загрузки посылок.
 */
@RequiredArgsConstructor
public class LoadUserCommand implements UserCommand {

    private final FileProcessingUtil fileProcessingUtil;

    /**
     * Выполняет команду загрузки посылок на основе переданной команды.
     *
     * @param command объект, содержащий параметры команды
     * @return результат выполнения команды
     * @throws ParcelLoadingException если произошла ошибка при загрузке посылок
     */
    @Override
    public String execute(ParsedCommand command) {
        try {
            String user = getUser(command);
            List<String> trucksFromArgs = parseTrucks(command.getTrucks());
            ParcelSourceType sourceType = determineParcelSourceType(command);
            return processParcels(command, trucksFromArgs, user, sourceType);
        } catch (Exception e) {
            throw new ParcelLoadingException("Ошибка при погрузке: " + e.getMessage(), e);
        }
    }

    /**
     * Проверяет и возвращает пользователя из команды.
     *
     * @param command объект с параметрами команды
     * @return имя пользователя
     * @throws UserNotProvidedException если пользователь не указан
     */
    private String getUser(ParsedCommand command) {
        String user = command.getUser();
        if (user == null || user.isEmpty()) {
            throw new UserNotProvidedException("Пользователь должен быть передан для команды LOAD");
        }
        return user;
    }

    /**
     * Парсит строки грузов в список.
     *
     * @param trucksText строка с грузами, разделенными запятыми
     * @return список грузов
     */
    private List<String> parseTrucks(String trucksText) {
        if (trucksText != null && !trucksText.isEmpty()) {
            return new ArrayList<>(List.of(trucksText.split(",")));
        }
        return new ArrayList<>();
    }

    /**
     * Определяет тип источника посылок на основе команды.
     *
     * @param command объект с параметрами команды
     * @return тип источника посылок
     */
    private ParcelSourceType determineParcelSourceType(ParsedCommand command) {
        if (isParcelsTextProvided(command)) {
            return ParcelSourceType.TEXT;
        } else if (isParcelsFileProvided(command)) {
            return ParcelSourceType.FILE;
        } else {
            throw new IllegalArgumentException("Укажите источник посылок (текст или файл)");
        }
    }

    /**
     * Обрабатывает источники посылок и выполняет соответствующую обработку.
     *
     * @param command        объект с параметрами команды
     * @param trucksFromArgs список грузов
     * @param user           имя пользователя
     * @param sourceType     тип источника посылок
     * @return результат обработки посылок
     */
    private String processParcels(ParsedCommand command, List<String> trucksFromArgs, String user, ParcelSourceType sourceType) {
        switch (sourceType) {
            case TEXT:
                return processParcelsFromText(command, trucksFromArgs, user);
            case FILE:
                return processParcelsFromFile(command, trucksFromArgs, user);
            default:
                throw new IllegalArgumentException("Неизвестный тип источника посылок");
        }
    }

    /**
     * Проверяет, предоставлен ли текст посылок.
     *
     * @param command объект с параметрами команды
     * @return true, если текст посылок предоставлен, иначе false
     */
    private boolean isParcelsTextProvided(ParsedCommand command) {
        String parcelsText = command.getParcelsText();
        return parcelsText != null && !parcelsText.isEmpty();
    }

    /**
     * Проверяет, предоставлен ли файл с посылками.
     *
     * @param command объект с параметрами команды
     * @return true, если файл с посылками предоставлен, иначе false
     */
    private boolean isParcelsFileProvided(ParsedCommand command) {
        String parcelsFile = command.getParcelsFile();
        return parcelsFile != null && !parcelsFile.isBlank();
    }

    /**
     * Обрабатывает посылки из текста.
     *
     * @param command        объект с параметрами команды
     * @param trucksFromArgs список грузов
     * @param user           имя пользователя
     * @return результат обработки посылок
     */
    private String processParcelsFromText(ParsedCommand command, List<String> trucksFromArgs, String user) {
        return fileProcessingUtil.processFile(
                null, // Path не требуется
                command.getParcelsText(),
                trucksFromArgs,
                command.isUseEasyAlgorithm(),
                command.isSaveToFile(),
                command.isUseEvenAlgorithm(),
                user
        );
    }

    /**
     * Обрабатывает посылки из файла.
     *
     * @param command        объект с параметрами команды
     * @param trucksFromArgs список грузов
     * @param user           имя пользователя
     * @return результат обработки посылок
     */
    private String processParcelsFromFile(ParsedCommand command, List<String> trucksFromArgs, String user) {
        return fileProcessingUtil.processFile(
                Path.of(command.getParcelsFile()),
                null, // Текст не требуется
                trucksFromArgs,
                command.isUseEasyAlgorithm(),
                command.isSaveToFile(),
                command.isUseEvenAlgorithm(),
                user
        );
    }
}
