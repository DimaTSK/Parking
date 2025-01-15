package org.hofftech.parking.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.CommandProcessingException;
import org.hofftech.parking.handler.CommandHandler;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.processor.CommandProcessor;
import org.hofftech.parking.factory.CommandProcessorFactory;

/**
 * Реализация интерфейса {@link CommandHandler} для обработки команд.
 * <p>
 * Этот класс отвечает за парсинг полученных команд, выбор соответствующего процессора
 * и выполнение команды. Если подходящий процессор не найден или возникает ошибка
 * во время обработки команды, генерируются соответствующие исключения.
 * </p>
 *
 * @author
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class CommandHandlerImpl implements CommandHandler {

    /**
     * Фабрика процессоров команд, используемая для получения соответствующего {@link CommandProcessor}
     * на основе типа команды.
     */
    private final CommandProcessorFactory processorFactory;

    /**
     * Парсер команд, используемый для преобразования входной строки команды в {@link ParsedCommand}.
     */
    private final CommandParser commandParser;

    /**
     * Обрабатывает заданную команду.
     * <p>
     * Метод выполняет следующие шаги:
     * <ol>
     *     <li>Парсит входную строку команды с помощью {@link CommandParser}.</li>
     *     <li>Получает соответствующий {@link CommandProcessor} из {@link CommandProcessorFactory} на основе типа команды.</li>
     *     <li>Если процессор найден, выполняет команду, иначе генерирует {@link CommandProcessingException}.</li>
     * </ol>
     * В случае возникновения исключений они логируются и оборачиваются в {@link CommandProcessingException}.
     * </p>
     *
     * @param command строка команды для обработки
     * @throws CommandProcessingException если возникает ошибка при обработке команды
     */
    @Override
    public void handle(String command) {
        try {
            ParsedCommand parsedCommand = commandParser.parse(command);
            CommandProcessor processor = processorFactory.getProcessor(parsedCommand.getCommandType());
            if (processor != null) {
                processor.execute(parsedCommand);
            } else {
                String errorMsg = "Процессор для команды не найден: " + parsedCommand.getCommandType();
                log.error(errorMsg);
                throw new CommandProcessingException(errorMsg);
            }
        } catch (Exception e) {
            log.error("Ошибка обработки команды: {}", e.getMessage(), e);
            throw e instanceof CommandProcessingException
                    ? (CommandProcessingException) e
                    : new CommandProcessingException("Ошибка обработки команды", e);
        }
    }
}
