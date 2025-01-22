package org.hofftech.parking.service.command;

import org.hofftech.parking.model.ParsedCommand;
/**
 * Интерфейс для выполнения пользовательских команд.
 */
public interface UserCommand {
    String execute(ParsedCommand command);
}