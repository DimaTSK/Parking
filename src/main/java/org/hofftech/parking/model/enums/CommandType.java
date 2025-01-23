package org.hofftech.parking.model.enums;

import lombok.Getter;

/**
 * Перечисление типов команд, используемых в системе.
 * <p>
 * Каждая константа данного перечисления представляет определённый тип команды,
 * который может быть выполнен пользователем или системой.
 * </p>
 *
 */
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