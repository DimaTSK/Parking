package org.hofftech.parking.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.parcer.ParcelJsonParser;
import org.hofftech.parking.validator.ParcelValidator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonReader {
    private final ObjectMapper objectMapper;
    private final ParcelValidator parcelValidator;
    private final ParcelJsonParser parcelJsonParser;

    public JsonReader(ParcelValidator parcelValidator,  ParcelJsonParser parcelJsonParser) {
        this.parcelValidator = parcelValidator;
        this.parcelJsonParser = parcelJsonParser;
        this.objectMapper = new ObjectMapper();
    }

    public List<String> importJson(String jsonFilePath) throws IOException {
        File jsonFile = new File(jsonFilePath);
        if (!jsonFile.exists()) {
            throw new IOException("Файл не найден: " + jsonFilePath);
        }

        Map<String, Object> jsonData = objectMapper.readValue(jsonFile, Map.class);
        parcelValidator.validateJsonStructure(jsonData);

        return parcelJsonParser.extractParcels(jsonData);
    }
}