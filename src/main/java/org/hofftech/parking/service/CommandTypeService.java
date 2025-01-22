package org.hofftech.parking.service;

import org.hofftech.parking.model.enums.CommandType;

public class CommandTypeService {

    public CommandType determineCommandType(String firstArgument) {
        if (firstArgument == null || firstArgument.isEmpty()) {
            throw new RuntimeException("Первый аргумент команды пуст");
        }
        try {
            return CommandType.valueOf(firstArgument);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестная команда: " + firstArgument, e);
        }
    }
}