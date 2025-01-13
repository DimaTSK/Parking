package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;

@RequiredArgsConstructor
@Slf4j
public class FindCommandProcessor implements CommandProcessor {
    private final ParcelRepository repository;

    @Override
    public void execute(ParsedCommand command) {
        String name = command.getName();
        if (name == null || name.isEmpty()) {
            log.error("Имя посылки не указано.");
            return;
        }

        Parcel pkg = repository.findPackage(name);
        if (pkg != null) {
            System.out.println("Найдена посылка: " + pkg);
        } else {
            log.error("Посылка с именем '{}' не найдена.", name);
        }
    }
}