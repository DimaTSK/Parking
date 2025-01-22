package org.hofftech.parking.service.command.impl;

import lombok.Getter;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.service.command.UserCommand;

@Getter
public class StartUserCommand implements UserCommand {

    @Override
    public String execute(ParsedCommand command) {
        return "Список команд в readme";
    }
}