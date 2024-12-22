package org.hofftech.parking.utill;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class FileReaderUtil {
    public List<String> readAllLines(Path filePath) throws IOException {
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
    }
}