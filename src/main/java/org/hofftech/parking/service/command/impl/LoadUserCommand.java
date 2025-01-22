package org.hofftech.parking.service.command.impl;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.exception.UserNotProvidedException;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.util.FileProcessingUtil;
import org.hofftech.parking.service.command.UserCommand;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class LoadUserCommand implements UserCommand {

    private final FileProcessingUtil fileProcessingUtil;

    @Override
    public String execute(ParsedCommand command) {
        String parcelsText = command.getParcelsText();
        String parcelsFile = command.getParcelsFile();
        String trucksText = command.getTrucks();
        String user = command.getUser();
        boolean useEasyAlgorithm = command.isUseEasyAlgorithm();
        boolean useEvenAlgorithm = command.isUseEvenAlgorithm();
        boolean saveToFile = command.isSaveToFile();

        try {
            List<String> trucksFromArgs = trucksText != null && !trucksText.isEmpty()
                    ? new ArrayList<>(List.of(trucksText.split(",")))
                    : new ArrayList<>();

            if (user == null || user.isEmpty()) {
                throw new UserNotProvidedException("Пользователь должен быть передан для комынды LOAD");
            }

            if (parcelsText != null && !parcelsText.isEmpty()) {
                return fileProcessingUtil.processFile(
                        null, parcelsText, trucksFromArgs, useEasyAlgorithm, saveToFile,
                        useEvenAlgorithm, user);
            } else if (parcelsFile != null && !parcelsFile.isBlank()) {
                return fileProcessingUtil.processFile(
                        Path.of(parcelsFile), null, trucksFromArgs, useEasyAlgorithm, saveToFile,
                        useEvenAlgorithm, user);
            } else {
                throw new IllegalArgumentException("Укажите источник посылок (текст или файл)");
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при погрузке: " + e.getMessage());
        }
    }
}