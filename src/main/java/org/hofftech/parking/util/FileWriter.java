package org.hofftech.parking.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class FileWriter {
    public static void saveParcelsToFile(List<String> parcels, String outputFilePath) throws IOException {
        File outputFile = new File(outputFilePath);
        try (BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(outputFile))) {
            for (String line : parcels) {
                writer.write(line);
                writer.newLine();
            }
            log.info("Посылки сохранены в файл: {}", outputFile.getAbsolutePath());
        }
    }
}