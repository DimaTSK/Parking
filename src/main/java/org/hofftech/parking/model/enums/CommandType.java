package org.hofftech.parking.model.enums;

import lombok.Getter;

@Getter
public enum CommandType {
    START,
    EXIT,
    CREATE,
    FIND,
    UPDATE,
    DELETE,
    LIST,
    LOAD,
    UNLOAD;

    public static CommandType fromCommand(String command) {
        if (command.startsWith("/start")) return START;
        if (command.startsWith("exit")) return EXIT;
        if (command.startsWith("create")) return CREATE;
        if (command.startsWith("find")) return FIND;
        if (command.startsWith("update")) return UPDATE;
        if (command.startsWith("delete")) return DELETE;
        if (command.startsWith("list")) return LIST;
        if (command.startsWith("load")) return LOAD;
        if (command.startsWith("unload")) return UNLOAD;
        throw new IllegalArgumentException("Неизвестная команда: " + command);
    }
}