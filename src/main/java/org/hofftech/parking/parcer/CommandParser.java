package org.hofftech.parking.parcer;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.model.enums.CommandType;
import org.hofftech.parking.service.CommandTypeSelectionService;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для парсинга пользовательских команд.
 * Предоставляет методы для извлечения параметров из строки команды и создания объекта {@link ParsedCommand}.
 */
@RequiredArgsConstructor
public class CommandParser {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\+([a-zA-Z]+),?\\s*(\"[^\"]+\"|[^+]+)");
    private static final String SAVE = "save";
    private static final String EASY = "easy";
    private static final String EVEN = "even";
    private static final String WITH_COUNT = "withCount";
    private static final String PARCELS_TEXT = "parcelsText";
    private static final String PARCELS_FILE = "parcelsFile";
    private static final String TRUCKS = "trucks";
    private static final String IN_FILE = "inFile";
    private static final String NAME = "name";
    private static final String OLD_NAME = "oldName";
    private static final String FORM = "form";
    private static final String SYMBOL = "symbol";
    private static final String USER_ID = "user";
    private static final String DATE_FROM = "from";
    private static final String DATE_TO = "to";

    private static final int GROUP_ONE = 1;
    private static final int GROUP_TWO = 2;
    private static final int FIRST_ARGUMENT_INDEX = 0;

    private static final String COMMAND_SPLIT_SYMBOL = " ";
    private static final String COMMAND_PREFIX_REGEX = "^/";
    private static final String COMMAND_SUFFIX_REGEX = ",$";
    private static final String EMPTY_STRING = "";

    private static final int SUBSTRING_START_OFFSET = 1;
    private static final int SUBSTRING_END_OFFSET = 1;
    private static final String DOUBLE_QUOTE = "\"";

    private final CommandTypeSelectionService commandTypeSelectionService;

    /**
     * Парсит строку команды и возвращает объект {@link ParsedCommand}.
     *
     * @param command строка команды для парсинга
     * @return объект {@link ParsedCommand}, содержащий разобранные параметры команды
     */
    public ParsedCommand parse(String command) {
        Map<String, String> parameters = extractParameters(command);
        String firstArgumentFromCommand = extractFirstArgument(command);

        CommandType commandType = commandTypeSelectionService.determineCommandType(firstArgumentFromCommand);

        ParsedCommand parsedCommand = createParsedCommand(parameters);
        parsedCommand.setCommandType(commandType);
        setOptionalParameters(parsedCommand, parameters);

        return parsedCommand;
    }

    /**
     * Извлекает параметры из строки команды на основе регулярного выражения.
     *
     * @param command строка команды
     * @return карта с ключами и значениями параметров
     */
    private Map<String, String> extractParameters(String command) {
        Map<String, String> parameters = new HashMap<>();
        Matcher matcher = COMMAND_PATTERN.matcher(command);

        while (matcher.find()) {
            String key = matcher.group(GROUP_ONE);
            String value = matcher.group(GROUP_TWO);

            if (isQuoted(value)) {
                value = stripQuotes(value);
            }
            value = removeTrailingComma(value).trim();
            parameters.put(key, value);
        }
        return parameters;
    }

    /**
     * Извлекает первый аргумент из команды.
     *
     * @param command строка команды
     * @return первый аргумент команды в верхнем регистре без префикса "/"
     */
    private String extractFirstArgument(String command) {
        String[] parts = command.split(COMMAND_SPLIT_SYMBOL);
        if (parts.length == 0) {
            return EMPTY_STRING;
        }
        return parts[FIRST_ARGUMENT_INDEX]
                .replaceFirst(COMMAND_PREFIX_REGEX, EMPTY_STRING)
                .toUpperCase();
    }

    /**
     * Проверяет, заключено ли значение в кавычки.
     *
     * @param value строка для проверки
     * @return true, если значение начинается и заканчивается кавычкой, иначе false
     */
    private boolean isQuoted(String value) {
        return value.startsWith(DOUBLE_QUOTE) && value.endsWith(DOUBLE_QUOTE);
    }

    /**
     * Удаляет кавычки из начала и конца строки.
     *
     * @param value строка с кавычками
     * @return строка без начальных и конечных кавычек
     */
    private String stripQuotes(String value) {
        return value.substring(SUBSTRING_START_OFFSET, value.length() - SUBSTRING_END_OFFSET);
    }

    /**
     * Удаляет запятую в конце строки, если она присутствует.
     *
     * @param value строка с потенциальной запятой на конце
     * @return строка без запятой на конце
     */
    private String removeTrailingComma(String value) {
        return value.replaceAll(COMMAND_SUFFIX_REGEX, EMPTY_STRING);
    }

    /**
     * Создает объект {@link ParsedCommand} на основе извлеченных параметров.
     *
     * @param parameters карта с ключами и значениями параметров
     * @return объект {@link ParsedCommand} с установленными параметрами
     */
    private ParsedCommand createParsedCommand(Map<String, String> parameters) {
        boolean isSaveToFile = parameters.containsKey(SAVE);
        boolean isEasyAlgorithm = parameters.containsKey(EASY);
        boolean isEvenAlgorithm = parameters.containsKey(EVEN);
        boolean isWithCount = parameters.containsKey(WITH_COUNT);

        String parcelsText = parameters.get(PARCELS_TEXT);
        String parcelsFile = parameters.get(PARCELS_FILE);
        String trucks = parameters.get(TRUCKS);
        String inFile = parameters.get(IN_FILE);
        String user = parameters.get(USER_ID);
        String dateFrom = parameters.get(DATE_FROM);
        String dateTo = parameters.get(DATE_TO);

        return new ParsedCommand(
                isSaveToFile,
                isEasyAlgorithm,
                isEvenAlgorithm,
                user,
                dateFrom,
                dateTo,
                parcelsText,
                parcelsFile,
                trucks,
                inFile,
                isWithCount
        );
    }

    /**
     * Устанавливает необязательные параметры в объект {@link ParsedCommand}.
     *
     * @param parsedCommand объект {@link ParsedCommand}, в который устанавливаются параметры
     * @param parameters     карта с ключами и значениями параметров
     */
    private void setOptionalParameters(ParsedCommand parsedCommand, Map<String, String> parameters) {
        parsedCommand.setName(parameters.get(NAME));
        parsedCommand.setOldName(parameters.get(OLD_NAME));
        parsedCommand.setForm(parameters.get(FORM));
        parsedCommand.setSymbol(parameters.get(SYMBOL));
    }
}
