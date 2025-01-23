package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.FileSavingException;

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
public final class FileSavingService {

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
     */
    public void saveParcels(List<Map<String, Long>> parcels, String outputFilePath, boolean isWithCount) {
        File outputFile = new File(outputFilePath);
        log.info("Начало записи в файл {} с подсчётом: {}", outputFilePath, isWithCount);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map<String, Long> map : parcels) {
                writeParcelMapToFile(map, writer, isWithCount);
            }
            log.info("Успешная запись в файл {}", outputFilePath);
        } catch (IOException e) {
            log.error("Ошибка записи в файл {}: {}", outputFilePath, e.getMessage());
            throw new FileSavingException("Не удалось сохранить посылки в файл: " + outputFilePath, e);
        }
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
     * @param isWithCount флаг, определяющий способ записи (с подсчётом или без)
     */
    private void writeParcelMapToFile(Map<String, Long> map, BufferedWriter writer, boolean isWithCount) {
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            if (isWithCount) {
                writeLineWithCount(writer, key, value);
            } else {
                writeLinesWithoutCount(writer, key, value);
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
     */
    private void writeLineWithCount(BufferedWriter writer, String key, Long value) {
        try {
            String line = String.format("%s - %d шт", key, value);
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            log.error("Ошибка записи строки с подсчётом для {}: {}", key, e.getMessage());
            throw new FileSavingException("Не удалось записать строку с подсчётом для: " + key, e);
        }
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
     */
    private void writeLinesWithoutCount(BufferedWriter writer, String key, Long value) {
        try {
            for (long i = 0; i < value; i++) {
                writer.write(key);
                writer.newLine();
            }
        } catch (IOException e) {
            log.error("Ошибка записи строк без подсчёта для {}: {}", key, e.getMessage());
            throw new FileSavingException("Не удалось записать строки без подсчёта для: " + key, e);
        }
    }
}
