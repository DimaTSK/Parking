package org.hofftech.parking.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hofftech.parking.model.enums.CommandType;

@Getter
@Setter
@RequiredArgsConstructor
public class ParsedCommand {
    private String name;
    private String oldName;
    private String form;
    private String symbol;

    private final boolean saveToFile;
    private final boolean useEasyAlgorithm;
    private final boolean useEvenAlgorithm;

    private final String parcelsText;
    private final String parcelsFile;
    private final String trucks;
    private final String inFile;
    private final boolean withCount;

    private CommandType commandType;
}