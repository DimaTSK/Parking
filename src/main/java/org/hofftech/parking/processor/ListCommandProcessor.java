package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ListCommandProcessor implements CommandProcessor {
    private final ParcelRepository repository;

    @Override
    public void execute(ParsedCommand command) {
        List<Parcel> parcels = repository.getAllPackages();
        if (parcels.isEmpty()) {
            log.info("Нет доступных посылок.");
        } else {
            log.info("Список всех посылок:");
            parcels.forEach(pkg -> System.out.println(pkg.toString()));
        }
    }
}