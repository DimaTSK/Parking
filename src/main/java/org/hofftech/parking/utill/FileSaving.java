package org.hofftech.parking.utill;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
public class FileSaving {
    public static void savePackagesToFile(List<String> packages, String outputFilePath) throws IOException {
        File outputFile = new File(outputFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String line : packages) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.info("Посылки успешно сохранены в файл: {}", outputFile.getAbsolutePath());
    }
}