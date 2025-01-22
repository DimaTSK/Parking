package org.hofftech.parking.service.command.impl;

import lombok.Getter;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.service.command.UserCommand;
/**
 * Класс реализации пользовательской команды для создания посылки.
 */
@Getter
public class StartUserCommand implements UserCommand {
    /**
     * Выполняет команду создания посылки на основе переданной команды.
     */
    @Override
    public String execute(ParsedCommand command) {
        return "Список команд в readme";
    }
}