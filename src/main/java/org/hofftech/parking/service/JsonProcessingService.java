package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.enums.ParcelType;
import org.hofftech.parking.model.dto.TruckDto;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.ParcelPositionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hofftech.parking.utill.ParcelValidator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonProcessingService {
    private static final String OUTPUT_DIRECTORY = "out";
    private static final String FILE_NAME = "parcels.json";
    private final ObjectMapper objectMapper;
    private final ParcelValidator parcelValidator;

    public JsonProcessingService(ParcelValidator parcelValidator) {
        this.parcelValidator = parcelValidator;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveToJson(List<TruckDto> truckDtos) {
        File outputFile = createOutputFile();
        if (outputFile == null) {
            return;
        }

        List<Map<String, Object>> trucksData = new ArrayList<>();
        for (int i = 0; i < truckDtos.size(); i++) {
            trucksData.add(createTruckMap(truckDtos.get(i), i + 1));
        }

        writeJsonToFile(outputFile, trucksData);
    }

    private File createOutputFile() {
        File outputDir = new File(OUTPUT_DIRECTORY);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            log.error("Не удалось создать папку для вывода Json");
            return null;
        }
        return new File(outputDir, FILE_NAME);
    }

    private Map<String, Object> createTruckMap(TruckDto truckDto, int truckId) {
        Map<String, Object> truckMap = new LinkedHashMap<>();
        truckMap.put("truck_id", truckId);
        truckMap.put("parcels", createParcelsData(truckDto));
        return truckMap;
    }

    private List<Map<String, Object>> createParcelsData(TruckDto truckDto) {
        List<Map<String, Object>> parcelsData = new ArrayList<>();
        for (ParcelDto pkg : truckDto.getParcelDtos()) {
            parcelsData.add(createParcelsMap(pkg));
        }
        return parcelsData;
    }

    private Map<String, Object> createParcelsMap(ParcelDto pkg) {
        Map<String, Object> parcelsMap = new LinkedHashMap<>();
        parcelsMap.put("id", pkg.getId());
        parcelsMap.put("type", pkg.getType().name());

        ParcelPositionDto position = pkg.getParcelPositionDto();
        if (position != null) {
            parcelsMap.put("position", Map.of("x", position.getX() + 1, "y", position.getY() + 1));
        } else {
            log.warn("У упаковки с ID {} отсутствует стартовая позиция", pkg.getId());
        }

        return parcelsMap;
    }

    private void writeJsonToFile(File outputFile, List<Map<String, Object>> trucksData) {
        try {
            objectMapper.writeValue(outputFile, Map.of("trucks", trucksData));
            log.info("Файл JSON создан: {}", outputFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Ошибка при записи JSON файла: {}", e.getMessage());
        }
    }

    public List<String> importJson(String jsonFilePath) throws IOException {
        File jsonFile = new File(jsonFilePath);
        if (!parcelValidator.isFileExists(jsonFile)) {
            log.error("Файл не найден: {}", jsonFile.getAbsolutePath());
            throw new IOException("Файл не найден: " + jsonFilePath);
        }

        Map<String, Object> jsonData = readJsonFile(jsonFile);
        validateJsonStructure(jsonData);

        return extractParcelsFromJson(jsonData);
    }

    private Map<String, Object> readJsonFile(File jsonFile) throws IOException {
        return objectMapper.readValue(jsonFile, Map.class);
    }

    private void validateJsonStructure(Map<String, Object> jsonData) throws IOException {
        if (!parcelValidator.isValidJsonStructure(jsonData)) {
            log.error("Структура Json некорректа!");
            throw new IOException("Структура Json некорректа!");
        }
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
                parcelsOutput.add(""); // Добавляем пустую строку для разделения пакетов
            }
        }
        return parcelsOutput;
    }
}