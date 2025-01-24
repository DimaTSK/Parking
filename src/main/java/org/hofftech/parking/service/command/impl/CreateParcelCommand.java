package org.hofftech.parking.service.command.impl;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.service.FormatterService;
import org.hofftech.parking.service.command.UserCommand;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Класс реализации пользовательской команды для создания посылки.
 */
@Component
@RequiredArgsConstructor
public class CreateParcelCommand implements UserCommand {

    private final ParcelRepository parcelRepository;
    private final FormatterService formatterService;

    /**
     * Выполняет команду создания посылки на основе переданной команды.
     *
     * @param command переданная команда
     * @return результат выполнения команды
     */
    @Override
    public String execute(ParsedCommand command) {
        List<Parcel> parcels = parcelRepository.findAllParcel();
        return formatterService.formatParcelListAsMarkdown(parcels);
    }

}
