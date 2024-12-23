package org.hofftech.parking.utill;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class FileReader {
    public List<String> readAllLines(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            log.error("Файла не существует: {}", filePath);
            throw new IOException("Файла не существует: " + filePath);
        }
        if (!Files.isReadable(filePath)) {
            log.error("Файл недоступен на чтения: {}", filePath);
            throw new IOException("Файл недоступен на чтения: " + filePath);
        }
        log.info("Чтение из файла: {}", filePath);
        return Files.readAllLines(filePath);
    }
}