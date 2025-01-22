package org.hofftech.parking.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.factory.CommandFactory;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.service.command.UserCommand;

@Slf4j
@RequiredArgsConstructor

public class CommandHandler {
    private final CommandFactory processorFactory;
    private final CommandParser commandParser;
    @Getter
    private UserCommand currentProcessor;

    public String handle(String command) {
        ParsedCommand parsedCommand = commandParser.parse(command);
        currentProcessor = processorFactory.createProcessor(parsedCommand.getCommandType());

        if (currentProcessor != null) {
            return currentProcessor.execute(parsedCommand);
        } else {
            throw new IllegalArgumentException("Процессор для команды не найден: " + parsedCommand.getCommandType());
        }
    }

}