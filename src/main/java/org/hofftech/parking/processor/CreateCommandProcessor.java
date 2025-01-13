package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.validator.ParcelValidator;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CreateCommandProcessor implements CommandProcessor {
    private final ParcelRepository repository;

    @Override
    public void execute(ParsedCommand command) {
        String name = command.getName();
        String form = command.getForm();
        char symbol = command.getSymbol() != null ? command.getSymbol().charAt(0) : ' ';

        if (name == null || form == null) {
            log.error("Недостаточно данных для создания посылки.");
            return;
        }

        List<String> shape = ParcelValidator.isAbleToParseForm(form);
        Parcel newParcel = new Parcel(name, shape, symbol, new ParcelStartPosition(0, 0));
        repository.addPackage(newParcel);

        log.info("Посылка '{}' успешно создана.", name);

        StringBuilder output = new StringBuilder();
        output.append("Форма посылки:\n");
        shape.forEach(line -> output.append(line).append("\n"));

        System.out.println(output.toString());
    }
}