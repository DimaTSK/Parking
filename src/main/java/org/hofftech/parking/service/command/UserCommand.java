package org.hofftech.parking.service.command;

import org.hofftech.parking.model.ParsedCommand;

public interface UserCommand {
    String execute(ParsedCommand command);
}