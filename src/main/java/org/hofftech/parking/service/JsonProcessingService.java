package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.enums.ParcelType;
import org.hofftech.parking.model.dto.TruckDto;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.ParcelPositionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonProcessingService {
    private static final String OUTPUT_DIRECTORY = "out";
    private static final String FILE_NAME = "trucks.json";
    private final ObjectMapper objectMapper;
    private final ValidatorService validatorService;

    public JsonProcessingService(ValidatorService validatorService) {
        this.validatorService = validatorService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveToJson(List<TruckDto> truckDtos) {
        File outputDir = new File(OUTPUT_DIRECTORY);
        if (!outputDir.exists()) {
            boolean dirCreated = outputDir.mkdirs();
            if (!dirCreated) {
                log.error("Не удалось создать папку для вывода Json");
                return;
            }
        }

        File outputFile = new File(outputDir, FILE_NAME);

        List<Map<String, Object>> trucksData = new ArrayList<>();
        for (TruckDto truckDto : truckDtos) {
            Map<String, Object> truckMap = new LinkedHashMap<>(); //Linked чтобы вывод в json был по порядку
            truckMap.put("truck_id", truckDtos.indexOf(truckDto) + 1);

            List<Map<String, Object>> packagesData = new ArrayList<>();
            for (ParcelDto pkg : truckDto.getParcelDtos()) {
                Map<String, Object> packageMap = new LinkedHashMap<>();
                packageMap.put("id", pkg.getId());
                packageMap.put("type", pkg.getType().name());

                ParcelPositionDto position = pkg.getParcelPositionDto();
                if (position != null) {
                    Map<String, Object> positionMap = new LinkedHashMap<>();
                    positionMap.put("x", position.getX() + 1);
                    positionMap.put("y", position.getY() + 1);
                    packageMap.put("position", positionMap);
                } else {
                    log.warn("У упаковки с ID {} отсутствует стартовая позиция", pkg.getId());
                }

                packagesData.add(packageMap);
            }

            truckMap.put("packages", packagesData);
            trucksData.add(truckMap);
        }

        try {
            objectMapper.writeValue(outputFile, Map.of("trucks", trucksData));
            log.info("JSON файл успешно создан: {}", outputFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Ошибка при записи JSON файла: {}", e.getMessage());
        }
    }

    public List<String> importJson(String jsonFilePath) throws IOException {
        File jsonFile = new File(jsonFilePath);
        if (!validatorService.isFileExists(jsonFile)) {
            log.error("Файл не найден: {}", jsonFile.getAbsolutePath());
            throw new IOException("Файл не найден: " + jsonFilePath);
        }
        Map<String, Object> jsonData = objectMapper.readValue(jsonFile, Map.class);

        if (!validatorService.isValidJsonStructure(jsonData)) {
            log.error("Структура Json некорректа!");
            throw new IOException("Структура Json некорректа!");
        }

        List<String> packagesOutput = new ArrayList<>();
        List<Map<String, Object>> trucks = (List<Map<String, Object>>) jsonData.get("trucks");
        for (Map<String, Object> truck : trucks) {
            List<Map<String, Object>> packages = (List<Map<String, Object>>) truck.get("packages");
            for (Map<String, Object> pkg : packages) {
                String type = (String) pkg.get("type");
                List<String> shape = ParcelType.valueOf(type).getShape();
                packagesOutput.addAll(shape);
                packagesOutput.add("");
            }
        }
        return packagesOutput;
    }
}