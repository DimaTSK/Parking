package org.hofftech.parking.service.command.impl;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.exception.ParcelNameException;
import org.hofftech.parking.exception.ParcelNotFoundException;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.service.command.UserCommand;
/**
 * Класс реализации пользовательской команды для создания посылки.
 */
@RequiredArgsConstructor
public class FindUserCommand implements UserCommand {

    private final ParcelRepository parcelRepository;
    /**
     * Выполняет команду создания посылки на основе переданной команды.
     */
    @Override
    public String execute(ParsedCommand command) {
        String name = command.getName();
        if (name == null || name.isEmpty()) {
            throw new ParcelNameException("Имя посылки не указано");
        }

        return parcelRepository.findParcel(name)
                .map(foundParcel -> "Найдена посылка: " + foundParcel)
                .orElseThrow(() -> new ParcelNotFoundException(
                        "Посылка с именем '" + name + "' не найдена."
                ));
    }
}