package org.hofftech.parking.processor;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.ParsedCommand;

@Slf4j
public class StartCommandProcessor implements CommandProcessor {

    @Override
    public void execute(ParsedCommand command) {
        log.info("Список команд в readme");
    }
}