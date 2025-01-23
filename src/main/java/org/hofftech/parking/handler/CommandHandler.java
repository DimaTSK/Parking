package org.hofftech.parking.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.factory.CommandFactory;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.service.command.UserCommand;
import org.springframework.stereotype.Component;

/**
 * Обработчик команд, отвечающий за управление процессом выполнения пользовательских команд.
 * <p>
 * Использует фабрику команд {@link CommandFactory} для создания соответствующих процессоров команд
 * и парсер {@link CommandParser} для разбора входных строк команд.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CommandHandler {
    private final CommandFactory processorFactory;
    private final CommandParser commandParser;

    @Getter
    private UserCommand currentProcessor;

    /**
     * Основной метод обработки команды без обработки исключений.
     *
     * @param command строковое представление команды для обработки
     * @return результат выполнения команды в виде строки
     * @throws IllegalArgumentException если не найден процессор для заданного типа команды
     */
    public String execute(String command) {
        ParsedCommand parsedCommand = commandParser.parse(command);
        currentProcessor = processorFactory.createProcessor(parsedCommand.getCommandType());

        if (currentProcessor != null) {
            return currentProcessor.execute(parsedCommand);
        } else {
            throw new IllegalArgumentException("Процессор для команды не найден: " + parsedCommand.getCommandType());
        }
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
    public String handleCommand(String command) {
        try {
            return execute(command);
        } catch (Exception e) {
            log.error("Ошибка обработки команды: {}", e.getMessage(), e);
            return "Ошибка: " + e.getMessage();
        }
    }
}