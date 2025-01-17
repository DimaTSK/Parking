package org.hofftech.parking.processor;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.ParsedCommand;

/**
 * Обработчик команды завершения работы приложения. Реализует интерфейс {@link CommandProcessor}.
 *
 * <p>Этот класс отвечает за обработку команды выхода из приложения.
 * При выполнении команды логирует сообщение о завершении работы и завершает работу приложения.</p>
 */
@Slf4j
public class ExitCommandProcessor implements CommandProcessor {

    /**
     * Выполняет обработку команды завершения работы приложения.
     *
     * <p>Метод выполняет следующие шаги:
     * <ul>
     *     <li>Логирует сообщение о завершении работы приложения.</li>
     *     <li>Вызывает {@code System.exit(0)} для завершения работы приложения.</li>
     * </ul>
     * </p>
     *
     * @param command объект {@link ParsedCommand}, содержащий данные для выполнения команды
     */
    @Override
    public void execute(ParsedCommand command) {
        log.info("Приложение завершает работу.");
        System.exit(0);
    }
}
