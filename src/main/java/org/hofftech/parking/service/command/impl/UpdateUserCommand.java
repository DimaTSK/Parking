package org.hofftech.parking.service.command.impl;

import lombok.AllArgsConstructor;
import org.hofftech.parking.exception.ParcelNotFoundException;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.validator.ParcelValidator;
import org.hofftech.parking.service.command.UserCommand;

import java.util.List;
import java.util.Optional;
/**
 * Класс реализации пользовательской команды для создания посылки.
 */
@AllArgsConstructor
public class UpdateUserCommand implements UserCommand {

    private static final int INDEX_OF_FIRST_SYMBOL = 0;
    private final ParcelRepository parcelRepository;
    private final ParcelValidator parcelValidator;
    /**
     * Выполняет команду создания посылки на основе переданной команды.
     */
    @Override
    public String execute(ParsedCommand command) {
        String currentName = command.getOldName();
        Parcel existingParcel = parcelRepository.findParcel(currentName)
                .orElseThrow(() -> new ParcelNotFoundException("Посылка с именем '" + currentName + "' не найдена."));

        String newName = Optional.ofNullable(command.getName())
                .orElse(existingParcel.getName());

        char newSymbol = Optional.ofNullable(command.getSymbol())
                .map(symbol -> symbol.charAt(INDEX_OF_FIRST_SYMBOL))
                .orElse(existingParcel.getSymbol());

        if (newSymbol != existingParcel.getSymbol()) {
            existingParcel.updateSymbol(newSymbol);
        }

        List<String> newShape = Optional.ofNullable(command.getForm())
                .map(parcelValidator::validateForm)
                .orElse(existingParcel.getShape());

        Parcel updatedParcel = new Parcel(
                newName,
                newShape,
                existingParcel.getSymbol(),
                existingParcel.getParcelStartPosition()
        );

        parcelRepository.editParcel(currentName, updatedParcel);

        StringBuilder output = new StringBuilder("Обновлённая посылка:\n");
        newShape.forEach(shape -> output.append(shape).append('\n'));
        return output.toString();
    }
}