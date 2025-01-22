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
    UNLOAD,
    BILLING
}