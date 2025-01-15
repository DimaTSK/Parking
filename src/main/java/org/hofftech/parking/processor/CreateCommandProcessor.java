package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.InvalidCommandException;
import org.hofftech.parking.exception.ParcelCreationException;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.validator.ParcelValidator;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CreateCommandProcessor implements CommandProcessor {
    private final ParcelRepository repository;

    private static final char DEFAULT_SYMBOL = ' ';

    @Override
    public void execute(ParsedCommand command) {
        try {
            String name = command.getName();
            String form = command.getForm();

            if (name == null || form == null) {
                throw new InvalidCommandException("Недостаточно данных для создания посылки.");
            }

            String symbolStr = command.getSymbol();
            char symbol = (symbolStr != null && !symbolStr.isEmpty()) ? symbolStr.charAt(0) : DEFAULT_SYMBOL;

            List<String> shape = ParcelValidator.isAbleToParseForm(form);
            Parcel newParcel = new Parcel(name, shape, symbol, new ParcelStartPosition(0, 0));
            repository.addPackage(newParcel);

            log.info("Посылка '{}' успешно создана.", name);

            StringBuilder output = new StringBuilder();
            output.append("Форма посылки:\n");
            shape.forEach(line -> output.append(line).append("\n"));

            System.out.println(output.toString());
        } catch (InvalidCommandException e) {
            log.error("Ошибка выполнения команды создания посылки: {}", e.getMessage());
            // Дополнительно можно выбросить или обработать исключение на более высоком уровне
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании посылки: {}", e.getMessage(), e);
            // Обработка других возможных исключений
            throw new ParcelCreationException("Не удалось создать посылку из-за внутренней ошибки.", e);
        }
    }
}
