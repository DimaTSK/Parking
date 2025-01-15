package org.hofftech.parking.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Утилитный класс для работы с файлами, предоставляющий методы чтения содержимого файлов.
 */
@Slf4j
public final class FileReaderUtil {

    /**
     * Читает все строки из указанного файла.
     *
     * <p>Метод выполняет следующие действия:
     * <ul>
     *     <li>Проверяет существование файла по заданному пути.</li>
     *     <li>Проверяет доступность файла для чтения.</li>
     *     <li>Читает все строки из файла и возвращает их в виде списка строк.</li>
     * </ul>
     *
     * @param filePath путь к файлу, из которого необходимо прочитать строки
     * @return список строк, содержащихся в указанном файле
     * @throws RuntimeException если файл не существует, недоступен для чтения или произошла ошибка при чтении
     */
    public static List<String> readAllLines(Path filePath) {
        try {
            if (!Files.exists(filePath)) {
                log.error("Файл не существует: {}", filePath);
                throw new IOException("Файл не существует: " + filePath);
            }
            if (!Files.isReadable(filePath)) {
                log.error("Файл недоступен для чтения: {}", filePath);
                throw new IOException("Файл недоступен для чтения: " + filePath);
            }
            log.info("Чтение строк из файла: {}", filePath);
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            log.error("Произошла ошибка чтения файла {}", filePath);
            throw new RuntimeException("Ошибка чтения файла: " + filePath, e);
        }
    }
}
