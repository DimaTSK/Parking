package org.hofftech.parking.util;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.InputFileException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public final class FileReaderUtil {
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
