package org.hofftech.parking.parcer;

import lombok.AllArgsConstructor;
import org.hofftech.parking.model.enums.CommandType;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.service.CommandTypeService;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class CommandParser {

    private static final String COMMAND_REGEX = "\\+([a-zA-Z]+),?\\s*(\"[^\"]+\"|[^+]+)";
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
    private static final int GROUP_ONE = 1;
    private static final int GROUP_TWO = 2;
    private static final int FIRST_ARGUMENT_INDEX = 0;
    private static final String COMMAND_SPLIT_SYMBOL = " ";
    private static final String USER_ID = "user";
    private static final String DATE_FROM = "from";
    private static final String DATE_TO = "to";

    private final CommandTypeService commandTypeService;

    public ParsedCommand parse(String command) {
        Map<String, String> parameters = extractParameters(command);
        String firstArgumentFromCommand = command.split(COMMAND_SPLIT_SYMBOL)[FIRST_ARGUMENT_INDEX].
                replaceFirst("^/", "").toUpperCase();

        CommandType commandType = commandTypeService.determineCommandType(firstArgumentFromCommand);

        ParsedCommand parsedCommand = createParsedCommand(parameters);
        parsedCommand.setCommandType(commandType);
        setOptionalParameters(parsedCommand, parameters);

        return parsedCommand;
    }

    private Map<String, String> extractParameters(String command) {
        Map<String, String> parameters = new HashMap<>();
        Pattern pattern = Pattern.compile(COMMAND_REGEX);
        Matcher matcher = pattern.matcher(command);

        while (matcher.find()) {
            String key = matcher.group(GROUP_ONE);
            String value = matcher.group(GROUP_TWO);

            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            value = value.replaceAll(",$", "").trim();
            parameters.put(key, value);
        }
        return parameters;
    }


    private ParsedCommand createParsedCommand(Map<String, String> parameters) {
        boolean saveToFile = parameters.containsKey(SAVE);
        boolean isEasyAlgorithm = parameters.containsKey(EASY);
        boolean isEvenAlgorithm = parameters.containsKey(EVEN);
        boolean withCount = parameters.containsKey(WITH_COUNT);

        String parcelsText = parameters.get(PARCELS_TEXT);
        String parcelsFile = parameters.get(PARCELS_FILE);
        String trucks = parameters.get(TRUCKS);
        String inFile = parameters.get(IN_FILE);
        String user = parameters.get(USER_ID);
        String dateFrom = parameters.get(DATE_FROM);
        String dateTo = parameters.get(DATE_TO);

        return new ParsedCommand(
                saveToFile,
                isEasyAlgorithm,
                isEvenAlgorithm,
                user,
                dateFrom,
                dateTo,
                parcelsText,
                parcelsFile,
                trucks,
                inFile,
                withCount
        );
    }

    private void setOptionalParameters(ParsedCommand parsedCommand, Map<String, String> parameters) {
        parsedCommand.setName(parameters.get(NAME));
        parsedCommand.setOldName(parameters.get(OLD_NAME));
        parsedCommand.setForm(parameters.get(FORM));
        parsedCommand.setSymbol(parameters.get(SYMBOL));
    }


}