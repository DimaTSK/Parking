package org.hofftech.parking.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;


import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Утилитный класс для сохранения информации о посылках в файл.
 * <p>
 * Предоставляет методы для записи списка посылок в указанный файл с возможностью
 * выбора формата записи (с подсчётом или без подсчёта).
 * </p>
 * Логирование осуществляется с помощью аннотации {@code @Slf4j}.
 * </p>
 */
@Slf4j
public final class FileSavingUtil {

    /**
     * Сохраняет список посылок в файл.
     *
     * <p>
     * Записывает каждую карту посылок в указанный файл. Формат записи зависит от параметра {@code isWithCount}.
     * Если {@code isWithCount} равен {@code true}, запись осуществляется с указанием количества каждой посылки.
     * В противном случае, запись производится без подсчёта.
     * </p>
     *
     * @param parcels        список карт, где каждая карта содержит название посылки и её количество
     * @param outputFilePath путь к выходному файлу для сохранения данных
     * @param isWithCount    флаг, указывающий, нужно ли записывать количество каждой посылки
     * @throws IOException если происходит ошибка при записи в файл
     */
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

    /**
     * Записывает карту посылок в файл.
     *
     * <p>
     * В зависимости от флага {@code withCount}, выбирает соответствующий метод записи.
     * </p>
     *
     * @param map       карта, содержащая название посылки и её количество
     * @param writer    {@link BufferedWriter} для записи данных в файл
     * @param withCount флаг, определяющий способ записи (с подсчётом или без)
     * @throws IOException если происходит ошибка при записи в файл
     */
    private void writeParcelMapToFile(Map<String, Long> map, BufferedWriter writer, boolean withCount) throws IOException {
        for (String key : map.keySet()) {
            if (withCount) {
                writeLineWithCount(writer, key, map.get(key));
            } else {
                writeLinesWithoutCount(writer, key, map.get(key));
            }
        }
    }

    /**
     * Записывает одну строку с количеством посылки в файл.
     *
     * <p>
     * Формат строки: {@code "название посылки - количество шт"}.
     * </p>
     *
     * @param writer {@link BufferedWriter} для записи данных в файл
     * @param key    название посылки
     * @param value количество посылки
     * @throws IOException если происходит ошибка при записи в файл
     */
    private void writeLineWithCount(BufferedWriter writer, String key, Long value) throws IOException {
        String line = String.format("%s - %d шт", key, value);
        writer.write(line);
        writer.newLine();
    }

    /**
     * Записывает несколько строк без указания количества посылки в файл.
     *
     * <p>
     * Для каждой единицы посылки записывается отдельная строка с её названием.
     * </p>
     *
     * @param writer {@link BufferedWriter} для записи данных в файл
     * @param key    название посылки
     * @param value количество посылки
     * @throws IOException если происходит ошибка при записи в файл
     */
    private void writeLinesWithoutCount(BufferedWriter writer, String key, Long value) throws IOException {
        for (int i = 0; i < value; i++) {
            writer.write(key);
            writer.newLine();
        }
    }
}
