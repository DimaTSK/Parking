package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;

@Slf4j
@RequiredArgsConstructor
public class DeleteCommandProcessor implements CommandProcessor {
    private final ParcelRepository repository;

    @Override
    public void execute(ParsedCommand command) {
        String name = command.getName();
        repository.deletePackage(name);

        log.info("Посылка '{}' успешно удалена.", name);
    }
}