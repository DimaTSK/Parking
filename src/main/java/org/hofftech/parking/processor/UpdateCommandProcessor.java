package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.validator.ParcelValidator;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class UpdateCommandProcessor implements CommandProcessor {
    private final ParcelRepository repository;
    private static final int FIRST_CHARACTER_INDEX = 0;

    @Override
    public void execute(ParsedCommand command) {
        String currentName = command.getOldName();
        Parcel existingParcel = repository.findPackage(currentName);

        if (existingParcel == null) {
            log.error("Посылка с именем '{}' не найдена.", currentName);
            return;
        }

        try {
            String newName = command.getName() != null ? command.getName() : existingParcel.getName();

            if (command.getSymbol() != null && !command.getSymbol().isEmpty()) {
                char newSymbol = command.getSymbol().charAt(FIRST_CHARACTER_INDEX);
                if (newSymbol != existingParcel.getSymbol()) {
                    existingParcel.updateSymbol(newSymbol);
                }
            }

            List<String> newShape = command.getForm() != null
                    ? ParcelValidator.parseAndValidateForm(command.getForm())
                    : existingParcel.getShape();

            Parcel updatedParcel = new Parcel(newName, newShape, existingParcel.getSymbol(), existingParcel.getParcelStartPosition());

            boolean nameChanged = !newName.equals(existingParcel.getName());

            if (nameChanged) {
                repository.deletePackage(currentName);
                repository.addPackage(updatedParcel);
                log.info("Посылка '{}' успешно переименована и обновлена.", currentName);
            } else {
                repository.editPackage(currentName, updatedParcel);
                log.info("Посылка '{}' успешно обновлена.", currentName);
            }

            StringBuilder output = new StringBuilder();
            output.append("Обновлённая посылка:\n");
            updatedParcel.getShape().forEach(shape -> output.append(shape).append("\n"));
            System.out.println(output.toString());
        } catch (IllegalArgumentException e) {
            log.error("Ошибка обновления посылки: {}", e.getMessage());
        }
    }
}

