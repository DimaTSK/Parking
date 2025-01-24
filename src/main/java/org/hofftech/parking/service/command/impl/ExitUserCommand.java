package org.hofftech.parking.service.command.impl;

import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.service.command.UserCommand;
/**
 * Класс реализации пользовательской команды для создания посылки.
 */
public class ExitUserCommand implements UserCommand {

    private static final int EXIT_SUCCESS = 0;

    /**
     * Выполняет команду завершения работы приложения.
     *
     * @param command Переданная команда
     * @return Сообщение о завершении работы
     */
    @Override
    public String execute(ParsedCommand command) {
        System.exit(EXIT_SUCCESS);
        return "Приложение завершает работу.";
    }
}