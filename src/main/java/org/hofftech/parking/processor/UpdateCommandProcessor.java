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
            boolean nameChanged = !newName.equals(existingParcel.getName());

            if (command.getSymbol() != null) {
                char newSymbol = command.getSymbol().charAt(0);
                if (newSymbol != existingParcel.getSymbol()) {
                    existingParcel.updateSymbol(newSymbol);
                }
            }

            List<String> newShape = command.getForm() != null ? ParcelValidator.isAbleToParseForm(command.getForm())
                    : existingParcel.getShape();

            Parcel updatedParcel = new Parcel(newName, newShape, existingParcel.getSymbol(), existingParcel.getParcelStartPosition());

            if (nameChanged) {
                repository.deletePackage(currentName);
                repository.addPackage(updatedParcel);
            } else {
                repository.editPackage(currentName, updatedParcel);
            }

            log.info("Посылка '{}' успешно обновлена.", currentName);

            StringBuilder output = new StringBuilder();
            output.append("Обновлённая посылка:\n");
            updatedParcel.getShape().forEach(shape -> output.append(shape).append("\n"));
            System.out.println(output.toString());
        } catch (IllegalArgumentException e) {
            log.error("Ошибка обновления посылки: {}", e.getMessage());
        }
    }
}