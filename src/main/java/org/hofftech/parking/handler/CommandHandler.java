package org.hofftech.parking.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.processor.CommandProcessor;
import org.hofftech.parking.processor.CommandProcessorFactory;

@Slf4j
@RequiredArgsConstructor
public class CommandHandler implements DefaultCommandHandler {
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
                log.error("Процессор для команды не найден: {}", parsedCommand.getCommandType());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки команды: {}", e.getMessage(), e);
        }
    }
}