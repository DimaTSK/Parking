package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.service.FileProcessingService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class LoadCommandProcessor implements CommandProcessor {
    private final FileProcessingService fileProcessingService;

    @Override
    public void execute(ParsedCommand command) {
        String parcelsText = command.getParcelsText();
        String parcelsFile = command.getParcelsFile();
        String trucksText = command.getTrucks();
        boolean useEasyAlgorithm = command.isUseEasyAlgorithm();
        boolean useEvenAlgorithm = command.isUseEvenAlgorithm();
        boolean saveToFile = command.isSaveToFile();

        List<String> trucksFromArgs = trucksText != null && !trucksText.isEmpty()
                ? new ArrayList<>(List.of(trucksText.split(",")))
                : new ArrayList<>();

        if (parcelsText != null && !parcelsText.isEmpty()) {
            fileProcessingService.processFile(null, parcelsText, trucksFromArgs, useEasyAlgorithm, saveToFile, useEvenAlgorithm);
        } else if (parcelsFile != null && !parcelsFile.isBlank()) {
            fileProcessingService.processFile(Path.of(parcelsFile), null, trucksFromArgs, useEasyAlgorithm, saveToFile, useEvenAlgorithm);
        } else {
            log.error("Ошибка: Укажите источник посылок (текст или файл)");
        }
    }
}