package org.hofftech.parking.processor;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.ParsedCommand;

/**
 * Обработчик команды начала работы. Реализует интерфейс {@link CommandProcessor}.
 *
 * <p>Этот класс отвечает за обработку команды начала работы, выводя информацию о доступных командах.</p>
 */
@Slf4j
public class StartCommandProcessor implements CommandProcessor {

    /**
     * Выполняет обработку команды начала работы, выводя список доступных команд.
     *
     * @param command объект {@link ParsedCommand}, содержащий данные для выполнения команды
     */
    @Override
    public void execute(ParsedCommand command) {
        log.info("Список команд в readme");
    }
}
