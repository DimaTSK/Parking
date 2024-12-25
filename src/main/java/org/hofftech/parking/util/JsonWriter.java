package org.hofftech.parking.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonWriter {
    private final ObjectMapper objectMapper;

    public JsonWriter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void writeToJsonFile(File outputFile, List<Map<String, Object>> trucksData) {
        try {
            objectMapper.writeValue(outputFile, Map.of("trucks", trucksData));
            log.info("Файл JSON создан: {}", outputFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Ошибка при записи JSON файла: {}", e.getMessage());
        }
    }
}
