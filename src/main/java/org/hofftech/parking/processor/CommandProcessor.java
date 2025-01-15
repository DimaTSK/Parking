package org.hofftech.parking.processor;

import org.hofftech.parking.model.ParsedCommand;

/**
 * Интерфейс для обработки различных команд в приложении.
 *
 * <p>Каждый обработчик команд должен реализовывать метод {@link #execute(ParsedCommand)}
 * для выполнения соответствующей логики команды.</p>
 */
public interface CommandProcessor {

    /**
     * Выполняет обработку заданной команды.
     *
     * <p>Метод принимает объект {@link ParsedCommand}, содержащий данные и параметры команды,
     * и выполняет соответствующие действия.</p>
     *
     * @param command объект {@link ParsedCommand}, содержащий данные для выполнения команды
     */
    void execute(ParsedCommand command);
}
