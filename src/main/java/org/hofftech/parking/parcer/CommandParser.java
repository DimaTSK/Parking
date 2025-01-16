package org.hofftech.parking.parcer;

import org.hofftech.parking.model.CommandFlags;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.model.enums.CommandType;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для разбора команд, вводимых пользователем.
 *
 * <p>Этот класс отвечает за парсинг входных строк команд, извлечение параметров и
 * создание объекта {@link ParsedCommand}, содержащего разобранные данные.</p>
 */
public class CommandParser {
    /**
     * Регулярное выражение для извлечения параметров команды.
     */
    private static final String PARAMETER_REGEX =
            "-(?<flag>[a-zA-Z]+)\\s+\"(?<valueQuoted>[^\"]+)\"|" +
                    "-(?<flagAlt>[a-zA-Z]+)\\s+(?<valueUnquoted>[^\\s]+)";
    private static final String GROUP_FLAG = "flag";
    private static final String GROUP_FLAG_ALT = "flagAlt";
    private static final String GROUP_VALUE_QUOTED = "valueQuoted";
    private static final String GROUP_VALUE_UNQUOTED = "valueUnquoted";

    /**
     * Компилированный шаблон регулярного выражения для извлечения параметров команды.
     */
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(PARAMETER_REGEX);

    /**
     * Парсит входную строку команды и создает объект {@link ParsedCommand}.
     *
     * <p>Метод выполняет следующие шаги:
     * <ul>
     *     <li>Извлекает параметры из команды.</li>
     *     <li>Определяет тип команды.</li>
     *     <li>Создает объект {@link ParsedCommand} на основе извлеченных параметров.</li>
     *     <li>Устанавливает дополнительные параметры для команды.</li>
     * </ul>
     * </p>
     *
     * @param command строка команды для парсинга
     * @return объект {@link ParsedCommand}, содержащий разобранные данные команды
     */
    public ParsedCommand parse(String command) {
        Map<String, String> parameters = extractParameters(command);
        CommandType commandType = CommandType.fromCommand(command);

        ParsedCommand parsedCommand = createParsedCommand(parameters);
        parsedCommand.setCommandType(commandType);
        setOptionalParameters(parsedCommand, parameters);

        return parsedCommand;
    }

    /**
     * Извлекает параметры из команды с помощью регулярного выражения.
     *
     * @param command строка команды для парсинга
     * @return карта параметров команды, где ключ - флаг, а значение - соответствующее значение
     */
    private Map<String, String> extractParameters(String command) {
        Map<String, String> parameters = new HashMap<>();

        Matcher matcher = PARAMETER_PATTERN.matcher(command);

        while (matcher.find()) {
            String flag = matcher.group(GROUP_FLAG);
            String value = matcher.group(GROUP_VALUE_QUOTED);

            if (flag != null && value != null) {
                parameters.put("-" + flag, value);
            } else {
                flag = matcher.group(GROUP_FLAG_ALT);
                value = matcher.group(GROUP_VALUE_UNQUOTED);
                if (flag != null && value != null) {
                    parameters.put("-" + flag, value);
                }
            }
        }

        return parameters;
    }

    /**
     * Создает объект {@link ParsedCommand} на основе извлеченных параметров.
     *
     * @param parameters карта параметров команды
     * @return объект {@link ParsedCommand} с установленными параметрами
     */
    private ParsedCommand createParsedCommand(Map<String, String> parameters) {
        boolean saveToFile = parameters.containsKey(CommandFlags.SAVE);
        boolean useEasyAlgorithm = parameters.containsKey(CommandFlags.EASY);
        boolean useEvenAlgorithm = parameters.containsKey(CommandFlags.EVEN);
        boolean withCount = parameters.containsKey(CommandFlags.WITH_COUNT);

        String parcelsText = parameters.get(CommandFlags.PARCELS_TEXT);
        String parcelsFile = parameters.get(CommandFlags.PARCELS_FILE);
        String trucks = parameters.get(CommandFlags.TRUCKS);
        String inFile = parameters.get(CommandFlags.IN_FILE);

        return new ParsedCommand(
                saveToFile,
                useEasyAlgorithm,
                useEvenAlgorithm,
                parcelsText,
                parcelsFile,
                trucks,
                inFile,
                withCount
        );
    }

    /**
     * Устанавливает дополнительные параметры для объекта {@link ParsedCommand}.
     *
     * @param parsedCommand объект {@link ParsedCommand}, которому устанавливаются параметры
     * @param parameters карта параметров команды
     */
    private void setOptionalParameters(ParsedCommand parsedCommand, Map<String, String> parameters) {
        parsedCommand.setName(parameters.get(CommandFlags.NAME));
        parsedCommand.setOldName(parameters.get(CommandFlags.OLD_NAME));
        parsedCommand.setForm(parameters.get(CommandFlags.FORM));
        parsedCommand.setSymbol(parameters.get(CommandFlags.SYMBOL));
    }
}
