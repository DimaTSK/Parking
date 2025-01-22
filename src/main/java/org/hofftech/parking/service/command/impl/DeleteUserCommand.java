package org.hofftech.parking.service.command.impl;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.exception.ParcelNameException;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.service.command.UserCommand;

@RequiredArgsConstructor
public class DeleteUserCommand implements UserCommand {

    private final ParcelRepository parcelRepository;

    @Override
    public String execute(ParsedCommand command) {
        String name = command.getName();
        if (name == null || name.isEmpty()) {
            throw new ParcelNameException("Имя посылки не указано.");
        }

        parcelRepository.deleteParcel(name);
        return "Посылка '" + name + "' успешно удалена.";
    }
}