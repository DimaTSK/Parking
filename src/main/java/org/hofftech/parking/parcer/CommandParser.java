package org.hofftech.parking.parcer;

import org.hofftech.parking.model.CommandFlags;
import org.hofftech.parking.model.enums.CommandType;
import org.hofftech.parking.model.ParsedCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommandParser {
    private static final String PARAMETER_REGEX =
            "-(?<flag>[a-zA-Z]+)\\s+\"(?<valueQuoted>[^\"]+)\"|" +
                    "-(?<flagAlt>[a-zA-Z]+)\\s+(?<valueUnquoted>[^\\s]+)";

    private static final Pattern PARAMETER_PATTERN = Pattern.compile(PARAMETER_REGEX);

    public ParsedCommand parse(String command) {
        Map<String, String> parameters = extractParameters(command);
        CommandType commandType = CommandType.fromCommand(command);

        ParsedCommand parsedCommand = createParsedCommand(parameters);
        parsedCommand.setCommandType(commandType);
        setOptionalParameters(parsedCommand, parameters);

        return parsedCommand;
    }

    private Map<String, String> extractParameters(String command) {
        Map<String, String> parameters = new HashMap<>();

        Matcher matcher = PARAMETER_PATTERN.matcher(command);

        while (matcher.find()) {
            String flag = matcher.group("flag");
            String value = matcher.group("valueQuoted");

            if (flag != null && value != null) {
                parameters.put("-" + flag, value);
            } else {
                flag = matcher.group("flagAlt");
                value = matcher.group("valueUnquoted");
                if (flag != null && value != null) {
                    parameters.put("-" + flag, value);
                }
            }
        }

        return parameters;
    }

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

    private void setOptionalParameters(ParsedCommand parsedCommand, Map<String, String> parameters) {
        parsedCommand.setName(parameters.get(CommandFlags.NAME));
        parsedCommand.setOldName(parameters.get(CommandFlags.OLD_NAME));
        parsedCommand.setForm(parameters.get(CommandFlags.FORM));
        parsedCommand.setSymbol(parameters.get(CommandFlags.SYMBOL));
    }
}
