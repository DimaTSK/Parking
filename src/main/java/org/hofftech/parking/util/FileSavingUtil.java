package org.hofftech.parking.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Slf4j
public final class FileSavingUtil {

    public static void savePackagesToFile(List<Map.Entry<String, Long>> packages,
                                          String outputFilePath, boolean withCount) throws IOException {
        File outputFile = new File(outputFilePath);
        log.info("Начинаем запись в файл {} количества", withCount ? "с подсчётом" : "без подсчёта");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map.Entry<String, Long> entry : packages) {
                String line;
                if (withCount) {
                    line = String.format("\"%s\" - %d шт", entry.getKey(), entry.getValue());
                } else {
                    for (int i = 0; i < entry.getValue(); i++) {
                        line = String.format("\"%s\"", entry.getKey());
                        writer.write(line);
                        writer.newLine();
                    }
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw e;
        }
        log.info("Посылки успешно импортированы и сохранены в файл: {}", outputFile.getAbsolutePath());
    }

}