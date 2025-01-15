package org.hofftech.parking.model.enums;

import lombok.Getter;

/**
 * Перечисление типов команд, поддерживаемых приложением.
 * Каждая константа представляет собой определённую команду, которую можно выполнить.
 */
@Getter
public enum CommandType {
    /**
     * Команда для запуска или инициации процесса.
     */
    START,

    /**
     * Команда для выхода из приложения или завершения процесса.
     */
    EXIT,

    /**
     * Команда для создания нового объекта или записи.
     */
    CREATE,

    /**
     * Команда для поиска существующего объекта или записи.
     */
    FIND,

    /**
     * Команда для обновления существующего объекта или записи.
     */
    UPDATE,

    /**
     * Команда для удаления существующего объекта или записи.
     */
    DELETE,

    /**
     * Команда для отображения списка объектов или записей.
     */
    LIST,

    /**
     * Команда для загрузки данных из источника.
     */
    LOAD,

    /**
     * Команда для выгрузки данных в назначенный пункт.
     */
    UNLOAD;

    /**
     * Преобразует строковое представление команды в соответствующий объект {@link CommandType}.
     *
     * @param command Строковое представление команды.
     * @return Соответствующий {@link CommandType}.
     * @throws IllegalArgumentException Если команда не распознана.
     */
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
