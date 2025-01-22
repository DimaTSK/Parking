package org.hofftech.parking.service.command.impl;

import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.service.command.UserCommand;
/**
 * Класс реализации пользовательской команды для создания посылки.
 */
public class ExitUserCommand implements UserCommand {
    /**
     * Выполняет команду создания посылки на основе переданной команды.
     */
    @Override
    public String execute(ParsedCommand command) {
        System.exit(0);
        return "Приложение завершает работу.";
    }
}