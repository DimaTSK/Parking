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

    public void saveParcelsToFile(List<Map<String, Long>> parcels, String outputFilePath, boolean isWithCount) throws IOException {
        File outputFile = new File(outputFilePath);
        log.info("Начинаем запись в файл {} количества: {}", outputFilePath, isWithCount ? "с подсчётом" : "без подсчёта");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map<String, Long> map : parcels) {
                writeParcelMapToFile(map, writer, isWithCount);
            }
        } catch (IOException e) {
            log.error("Ошибка записи в файл: {}", e.getMessage());
            throw e;
        }

        log.info("Посылки успешно импортированы и сохранены в файл: {}", outputFile.getAbsolutePath());
    }

    private void writeParcelMapToFile(Map<String, Long> map, BufferedWriter writer, boolean withCount) throws IOException {
        for (String key : map.keySet()) {
            if (withCount) {
                writeLineWithCount(writer, key, map.get(key));
            } else {
                writeLinesWithoutCount(writer, key, map.get(key));
            }
        }
    }

    private void writeLineWithCount(BufferedWriter writer, String key, Long value) throws IOException {
        String line = String.format("%s - %d шт", key, value);
        writer.write(line);
        writer.newLine();
    }

    private void writeLinesWithoutCount(BufferedWriter writer, String key, Long value) throws IOException {
        for (int i = 0; i < value; i++) {
            writer.write(key);
            writer.newLine();
        }
    }
}