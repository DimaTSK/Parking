package org.hofftech.parking.util;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.InputFileException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Утилитный класс для чтения данных из файла.
 * <p>
 * Предоставляет методы для чтения всех строк из указанного файла с проверкой
 * существования и доступности файла для чтения.
 * </p>
 * Логирование осуществляется с помощью аннотации {@code @Slf4j}.
 * </p>
 */
@Slf4j
public final class FileReaderUtil {

    /**
     * Читает все строки из файла по указанному пути.
     *
     * <p>
     * Проверяет, существует ли файл и доступен ли он для чтения. Если файл не существует
     * или недоступен, выбрасывается {@code InputFileException}. В случае успешного
     * чтения строк, возвращается список строк из файла.
     * </p>
     *
     * @param filePath путь к файлу для чтения
     * @return список строк, прочитанных из файла
     * @throws InputFileException если файл не существует, недоступен для чтения или происходит ошибка ввода/вывода
     */
    public static List<String> readAllLines(Path filePath) {
        try {
            if (!Files.exists(filePath)) {
                throw new InputFileException("Файл не существует: " + filePath);
            }
            if (!Files.isReadable(filePath)) {
                throw new InputFileException("Файл недоступен для чтения: " + filePath);
            }
            log.info("Чтение строк из файла: {}", filePath);
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new InputFileException("Ошибка чтения файла: " + filePath);
        }
    }
}
