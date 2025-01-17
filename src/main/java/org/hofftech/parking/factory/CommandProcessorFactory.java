package org.hofftech.parking.factory;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.model.enums.CommandType;
import org.hofftech.parking.processor.*;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.service.FileProcessingService;
import org.hofftech.parking.service.JsonProcessingService;

/**
 * Фабрика процессоров команд.
 * <p>
 * Этот класс отвечает за создание экземпляров {@link CommandProcessor} на основе типа команды,
 * определённого в {@link CommandType}. Использует репозиторий посылок и сервисы обработки файлов
 * и JSON для инициализации соответствующих процессоров.
 * </p>
 *
 * @author
 * @version 1.0
 */
@RequiredArgsConstructor
public class CommandProcessorFactory {

    /**
     * Репозиторий для управления посылками.
     */
    private final ParcelRepository repository;

    /**
     * Сервис для обработки файлов.
     */
    private final FileProcessingService fileProcessingService;

    /**
     * Сервис для обработки JSON данных.
     */
    private final JsonProcessingService jsonProcessingService;

    /**
     * Возвращает соответствующий {@link CommandProcessor} в зависимости от типа команды.
     * <p>
     * Метод использует оператор {@code switch} для определения, какой процессор создать,
     * основываясь на значении {@code commandType}. Для каждой области применения команды
     * создаётся соответствующий процессор.
     * </p>
     *
     * @param commandType тип команды, определённый в {@link CommandType}
     * @return экземпляр {@link CommandProcessor}, соответствующий указанному типу команды
     * @throws IllegalArgumentException если {@code commandType} не распознан
     */
    public CommandProcessor getProcessor(CommandType commandType) {
        return switch (commandType) {
            case CREATE -> new CreateCommandProcessor(repository);
            case FIND -> new FindCommandProcessor(repository);
            case UPDATE -> new UpdateCommandProcessor(repository);
            case DELETE -> new DeleteCommandProcessor(repository);
            case LIST -> new ListCommandProcessor(repository);
            case LOAD -> new LoadCommandProcessor(fileProcessingService);
            case UNLOAD -> new UnloadCommandProcessor(jsonProcessingService);
            case START -> new StartCommandProcessor();
            case EXIT -> new ExitCommandProcessor();
        };
    }
}
