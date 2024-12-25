package org.hofftech.parking.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.enums.ParcelType;
import org.hofftech.parking.validator.ParcelValidator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonReader {
    private final ObjectMapper objectMapper;
    private final ParcelValidator parcelValidator;

    public JsonReader(ParcelValidator parcelValidator) {
        this.parcelValidator = parcelValidator;
        this.objectMapper = new ObjectMapper();
    }

    public List<String> importJson(String jsonFilePath) throws IOException {
        File jsonFile = new File(jsonFilePath);
        if (!jsonFile.exists()) {
            throw new IOException("Файл не найден: " + jsonFilePath);
        }

        Map<String, Object> jsonData = objectMapper.readValue(jsonFile, Map.class);
        parcelValidator.validateJsonStructure(jsonData);

        return extractParcelsFromJson(jsonData);
    }

    private List<String> extractParcelsFromJson(Map<String, Object> jsonData) {
        List<String> parcelsOutput = new ArrayList<>();
        List<Map<String, Object>> trucks = (List<Map<String, Object>>) jsonData.get("trucks");
        for (Map<String, Object> truck : trucks) {
            List<Map<String, Object>> parcels = (List<Map<String, Object>>) truck.get("parcels");
            for (Map<String, Object> pkg : parcels) {
                String type = (String) pkg.get("type");
                List<String> shape = ParcelType.valueOf(type).getShape();
                parcelsOutput.addAll(shape);
                parcelsOutput.add("");
            }
        }
        return parcelsOutput;
    }
}
