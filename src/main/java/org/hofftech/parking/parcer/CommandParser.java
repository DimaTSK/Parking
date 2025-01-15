package org.hofftech.parking.parcer;

import org.hofftech.parking.model.enums.CommandType;
import org.hofftech.parking.model.ParsedCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {
    private static final String PARAMETER_REGEX =
            "-(?<flag>[a-zA-Z]+)\\s+\"(?<valueQuoted>[^\"]+)\"|-(?<flagAlt>[a-zA-Z]+)\\s+(?<valueUnquoted>[^\\s]+)";

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
        boolean saveToFile = parameters.containsKey("-save");
        boolean useEasyAlgorithm = parameters.containsKey("-easy");
        boolean useEvenAlgorithm = parameters.containsKey("-even");
        boolean withCount = parameters.containsKey("-withCount");

        String parcelsText = parameters.get("-parcelsText");
        String parcelsFile = parameters.get("-parcelsFile");
        String trucks = parameters.get("-trucks");
        String inFile = parameters.get("-inFile");

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
        parsedCommand.setName(parameters.get("-name"));
        parsedCommand.setOldName(parameters.get("-oldName"));
        parsedCommand.setForm(parameters.get("-form"));
        parsedCommand.setSymbol(parameters.get("-symbol"));
    }

}
