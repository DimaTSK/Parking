package org.hofftech.parking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.factory.CommandFactory;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.parcer.CommandParser;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Контроллер командного интерфейса (Shell), отвечающий за обработку пользовательских команд через консоль.
 * <p>
 * Использует {@link CommandFactory} для создания и выполнения команд и {@link CommandParser}
 * для разбора строковых представлений команд.
 * </p>
 */
@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class CommandController {

    private final CommandFactory processorFactory;
    private final CommandParser commandParser;

    /**
     * Стартовая команда, выводящая приветственное сообщение и список доступных команд.
     *
     * @return приветственное сообщение пользователю
     */
    @ShellMethod("Стартовая команда")
    public String start() {
        return "Привет!Это приложение по перекладыванию посылочек.";
    }

    /**
     * Команда для выхода из приложения.
     * <p>
     * Завершает работу приложения с кодом 0.
     * </p>
     *
     * @return сообщение о завершении работы приложения (на самом деле не достигается из-за завершения JVM)
     */
    @ShellMethod("Выход из приложения")
    public String exit() {
        System.exit(0);
        return "Приложение завершило работу.";
    }

    /**
     * Команда для поиска посылки с указанными аргументами.
     *
     * @param args аргументы для поиска посылки
     * @return результат выполнения команды поиска
     */
    @ShellMethod("Поиск посылки")
    public String find(@ShellOption(defaultValue = "") String args) {
        return handleCommand("find " + args);
    }

    /**
     * Команда для создания новой посылки с указанными аргументами.
     *
     * @param args аргументы для создания посылки
     * @return результат выполнения команды создания
     */
    @ShellMethod("Создание новой посылки")
    public String create(@ShellOption(defaultValue = "") String args) {
        return handleCommand("create " + args);
    }

    /**
     * Команда для обновления существующей посылки с указанными аргументами.
     *
     * @param args аргументы для обновления посылки
     * @return результат выполнения команды обновления
     */
    @ShellMethod("Обновление существующей посылки")
    public String update(@ShellOption(defaultValue = "") String args) {
        return handleCommand("update " + args);
    }

    /**
     * Команда для удаления посылки с указанными аргументами.
     *
     * @param args аргументы для удаления посылки
     * @return результат выполнения команды удаления
     */
    @ShellMethod("Удаление посылки")
    public String delete(@ShellOption(defaultValue = "") String args) {
        return handleCommand("delete " + args);
    }

    /**
     * Команда для вывода списка всех посылок.
     *
     * @return список всех посылок
     */
    @ShellMethod("Список всех посылок")
    public String list() {
        return handleCommand("list");
    }

    /**
     * Команда для загрузки данных с указанными аргументами.
     *
     * @param args аргументы для загрузки данных
     * @return результат выполнения команды загрузки
     */
    @ShellMethod("Погрузка")
    public String load(@ShellOption(defaultValue = "") String args) {
        return handleCommand("load " + args);
    }

    /**
     * Команда для разгрузки данных с указанными аргументами.
     *
     * @param args аргументы для разгрузки данных
     * @return результат выполнения команды разгрузки
     */
    @ShellMethod("Разгрузка")
    public String unload(@ShellOption(defaultValue = "") String args) {
        return handleCommand("unload " + args);
    }

    /**
     * Команда для генерации отчета биллинга с указанными аргументами.
     *
     * @param args аргументы для генерации отчета биллинга
     * @return результат выполнения команды биллинга
     */
    @ShellMethod("Генерация отчета биллинга")
    public String billing(@ShellOption(defaultValue = "") String args) {
        return handleCommand("billing " + args);
    }

    /**
     * Обрабатывает переданную команду, выполняет её и возвращает результат.
     * <p>
     * Парсит строковую команду, создает соответствующий процессор команды через {@link CommandFactory},
     * выполняет команду и возвращает результат. В случае возникновения ошибки, логирует её и возвращает сообщение об ошибке.
     * </p>
     *
     * @param command строковое представление команды для обработки
     * @return результат выполнения команды или сообщение об ошибке
     */
    private String handleCommand(String command) {
        try {
            ParsedCommand parsedCommand = commandParser.parse(command);
            return processorFactory.createProcessor(parsedCommand.getCommandType())
                    .execute(parsedCommand);
        } catch (Exception e) {
            log.error("Ошибка обработки команды: {}", e.getMessage(), e);
            return "Ошибка: " + e.getMessage();
        }
    }
}
