package org.hofftech.parking.service.command.impl;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.exception.ParcelArgumentException;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.validator.ParcelValidator;
import org.hofftech.parking.service.command.UserCommand;
import java.util.List;
/**
 * Класс реализации пользовательской команды для создания посылки.
 */
@RequiredArgsConstructor
public class CreateUserCommand implements UserCommand {

    private final ParcelRepository parcelRepository;
    private final ParcelValidator parcelValidator;

    private static final int FIRST_CHAR_INDEX = 0;
    private static final int POSITION_START_INDEX = 0;
    /**
     * Выполняет команду создания посылки на основе переданной команды.
     */
    @Override
    public String execute(ParsedCommand command) {
        String name = command.getName();
        String form = command.getForm();
        char symbol = command.getSymbol() != null ? command.getSymbol().charAt(FIRST_CHAR_INDEX) : ' ';

        if (name == null || form == null) {
            throw new ParcelArgumentException("Недостаточно данных для создания посылки");
        }

        List<String> shape = parcelValidator.validateForm(form);
        Parcel newParcel = new Parcel(name, shape, symbol, new ParcelStartPosition(POSITION_START_INDEX, POSITION_START_INDEX));
        parcelRepository.addParcel(newParcel);

        StringBuilder output = new StringBuilder("Создана посылка " + newParcel.getName() + "\nФорма посылки:\n");
        shape.forEach(line -> output.append(line).append("\n"));
        return output.toString();
    }
}