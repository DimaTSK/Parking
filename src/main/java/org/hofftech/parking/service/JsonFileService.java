package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.DirectoryCreationException;
import org.hofftech.parking.model.enums.CommandConstants;
import org.hofftech.parking.model.entity.Truck;
import org.hofftech.parking.util.JsonReader;
import org.hofftech.parking.util.JsonWriter;
import org.hofftech.parking.util.mapper.TruckDataMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonFileService {
    private static final String FILE_NAME = "parcels.json";
    private final JsonWriter jsonWriter;
    private final JsonReader jsonReader;
    private final TruckDataMapper truckDataMapper;

    public JsonFileService(JsonWriter jsonWriter, JsonReader jsonReader, TruckDataMapper truckDataMapper) {
        this.jsonWriter = jsonWriter;
        this.jsonReader = jsonReader;
        this.truckDataMapper = truckDataMapper;
    }

    public void saveTrucksToJson(List<Truck> truckEntities) {
        File outputFile = createOutputFile();
        List<Map<String, Object>> trucksData = truckDataMapper.mapTrucks(truckEntities);
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
}
