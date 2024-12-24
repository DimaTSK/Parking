package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.DirectoryCreationException;
import org.hofftech.parking.exception.MissingParcelPositionException;
import org.hofftech.parking.model.enums.CommandConstants;
import org.hofftech.parking.model.enums.ParcelType;
import org.hofftech.parking.model.dto.TruckDto;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.ParcelPosition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hofftech.parking.utill.JsonReader;
import org.hofftech.parking.utill.JsonWriter;
import org.hofftech.parking.utill.ParcelValidator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonProcessingService {
    private static final String FILE_NAME = "parcels.json";
    private final JsonWriter jsonWriter;
    private final JsonReader jsonReader;

    public JsonProcessingService(ParcelValidator parcelValidator) {
        this.jsonWriter = new JsonWriter();
        this.jsonReader = new JsonReader(parcelValidator);
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

        jsonWriter.writeToJsonFile(outputFile, trucksData);
    }

    public List<String> importJson(String jsonFilePath) throws IOException {
        return jsonReader.importJson(jsonFilePath);
    }

    private File createOutputFile() {
        File outputDir = new File(CommandConstants.OUTPUT_DIRECTORY.getValue());
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new DirectoryCreationException("Не удалось создать папку для вывода JSON: " + CommandConstants.OUTPUT_DIRECTORY.getValue());
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

        ParcelPosition position = pkg.getParcelPosition();
        if (position != null) {
            parcelsMap.put("position", Map.of("x", position.getX() + 1, "y", position.getY() + 1));
        } else {
            throw new MissingParcelPositionException("У упаковки с ID " + pkg.getId() + " отсутствует стартовая позиция");
        }

        return parcelsMap;
    }
}

