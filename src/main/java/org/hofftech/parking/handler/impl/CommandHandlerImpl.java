package org.hofftech.parking.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.CommandProcessingException;
import org.hofftech.parking.handler.CommandHandler;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.processor.CommandProcessor;
import org.hofftech.parking.factory.CommandProcessorFactory;

@Slf4j
@RequiredArgsConstructor
public class CommandHandlerImpl implements CommandHandler {
    private final CommandProcessorFactory processorFactory;
    private final CommandParser commandParser;

    @Override
    public void handle(String command) {
        try {
            ParsedCommand parsedCommand = commandParser.parse(command);
            CommandProcessor processor = processorFactory.getProcessor(parsedCommand.getCommandType());
            if (processor != null) {
                processor.execute(parsedCommand);
            } else {
                String errorMsg = "Процессор для команды не найден: " + parsedCommand.getCommandType();
                log.error(errorMsg);
                throw new CommandProcessingException(errorMsg);
            }
        } catch (Exception e) {
            log.error("Ошибка обработки команды: {}", e.getMessage(), e);
            throw e instanceof CommandProcessingException
                    ? (CommandProcessingException) e
                    : new CommandProcessingException("Ошибка обработки команды", e);
        }
    }
}
