package org.hofftech.parking.service.command.impl;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.service.command.UserCommand;

import java.util.List;
/**
 * Класс реализации пользовательской команды для создания посылки.
 */
@RequiredArgsConstructor
public class ListUserCommand implements UserCommand {

    private final ParcelRepository parcelRepository;
    /**
     * Выполняет команду создания посылки на основе переданной команды.
     */
    @Override
    public String execute(ParsedCommand command) {
        List<Parcel> parcels = parcelRepository.getAllParcel();
        if (parcels.isEmpty()) {
            return "Нет доступных посылок.";
        } else {
            StringBuilder output = new StringBuilder("Список всех посылок:\n");
            parcels.forEach(parcel -> output.append(parcel).append("\n"));
            return output.toString();
        }
    }

}