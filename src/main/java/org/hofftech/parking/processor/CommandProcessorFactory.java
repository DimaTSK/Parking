package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.model.enums.CommandType;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.service.FileProcessingService;
import org.hofftech.parking.service.JsonProcessingService;


@RequiredArgsConstructor
public class CommandProcessorFactory {
    private final ParcelRepository repository;
    private final FileProcessingService fileProcessingService;
    private final JsonProcessingService jsonProcessingService;

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