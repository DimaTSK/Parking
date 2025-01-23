package org.hofftech.parking.service;

import org.hofftech.parking.exception.EmptyCommandArgumentException;
import org.hofftech.parking.exception.UnknownCommandTypeException;
import org.hofftech.parking.model.enums.CommandType;

/**
 * Сервис для определения типа команды на основе первого аргумента.
 * Предоставляет метод для преобразования строки в соответствующий {@link CommandType}.
 */
public class CommandTypeSelectionService {

        /**
         * Определяет тип команды на основе переданного первого аргумента.
         *
         * @param firstArgument Первый аргумент команды в виде строки.
         * @return {@link CommandType} соответствующий переданному аргументу.
         * @throws EmptyCommandArgumentException Если первый аргумент команды пуст или равен {@code null}.
         * @throws UnknownCommandTypeException   Если первый аргумент не соответствует ни одному значению {@link CommandType}.
         */
        public CommandType determineCommandType(String firstArgument) {
            if (firstArgument == null || firstArgument.isEmpty()) {
                throw new EmptyCommandArgumentException();
            }
            try {
                return CommandType.valueOf(firstArgument);
            } catch (IllegalArgumentException e) {
                throw new UnknownCommandTypeException(firstArgument, e);
            }
        }
    }