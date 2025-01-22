package org.hofftech.parking.service.command.impl;

import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.service.command.UserCommand;

public class ExitUserCommand implements UserCommand {

    @Override
    public String execute(ParsedCommand command) {
        System.exit(0);
        return "Приложение завершает работу.";
    }
}