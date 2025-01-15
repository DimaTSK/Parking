package org.hofftech.parking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.TruckDto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;


@Slf4j
public class JsonProcessingService {
    private static final String OUTPUT_DIRECTORY = "out";
    private static final String FILE_NAME = "trucks.json";
    private static final String KEY_TRUCKS = "trucks";


    private static final int POSITION_OFFSET = 1;

    private final ObjectMapper objectMapper;

    public JsonProcessingService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String saveToJson(List<Truck> trucks) {
        File outputFile = createFile();
        List<TruckDto> trucksData = new ArrayList<>();
        for (int i = 0; i < trucks.size(); i++) {
            trucksData.add(convertToTruckDto(trucks.get(i), i));
        }

        try {
            Map<String, Object> dataMap = Map.of(KEY_TRUCKS, trucksData);
            String jsonString = objectMapper.writeValueAsString(dataMap);
            objectMapper.writeValue(outputFile, dataMap);
            log.info("JSON файл успешно создан: {}", outputFile.getAbsolutePath());
            return jsonString;
        } catch (IOException e) {
            log.error("Ошибка при записи JSON: {}", e.getMessage());
            throw new RuntimeException("Не удалось сохранить данные в JSON", e);
        }
    }

    public List<Map.Entry<String, Long>> importPackagesFromJson(String jsonFilePath, boolean withCount) {
        File jsonFile = new File(jsonFilePath);
        if (!jsonFile.exists()) {
            log.error("Файл не найден: {}", jsonFile.getAbsolutePath());
            throw new RuntimeException("Файл не найден: " + jsonFilePath);
        }

        Map<String, List<TruckDto>> jsonData;
        try {
            jsonData = objectMapper.readValue(
                    jsonFile,
                    new TypeReference<Map<String, List<TruckDto>>>() {}
            );
        } catch (IOException e) {
            log.error("Ошибка при чтении JSON файла: {}", e.getMessage());
            throw new RuntimeException("Не удалось прочитать JSON файл", e);
        }

        List<Parcel> parcels = new ArrayList<>();
        List<TruckDto> trucks = jsonData.get(KEY_TRUCKS); // Использование константы
        if (trucks != null) {
            for (TruckDto truck : trucks) {
                extractPackagesFromTruck(truck, parcels);
            }
        } else {
            log.warn("Ключ '{}' отсутствует в JSON файле", KEY_TRUCKS);
        }

        if (withCount) {
            return new ArrayList<>(parcels.stream()
                    .collect(Collectors.groupingBy(
                            Parcel::getName,
                            LinkedHashMap::new,
                            Collectors.counting()
                    ))
                    .entrySet());
        } else {
            return parcels.stream()
                    .map(pkg -> Map.entry(pkg.getName(), 1L))
                    .collect(Collectors.toList());
        }
    }

    private void extractPackagesFromTruck(TruckDto truck, List<Parcel> parcels) {
        for (ParcelDto pkgDto : truck.getPackages()) {
            ParcelStartPosition position = null;
            if (pkgDto.getStartPosition() != null) {
                position = new ParcelStartPosition(
                        pkgDto.getStartPosition().getX(),
                        pkgDto.getStartPosition().getY()
                );
            }
            Parcel pkg = new Parcel(
                    pkgDto.getName(),
                    pkgDto.getShape(),
                    pkgDto.getSymbol(),
                    position
            );
            parcels.add(pkg);
        }
    }

    private TruckDto convertToTruckDto(Truck truck, int truckIndex) {
        TruckDto truckDto = new TruckDto();
        truckDto.setTruckId(truckIndex + 1);
        truckDto.setTruckSize(truck.getWidth() + "x" + truck.getHeight());

        List<ParcelDto> parcelDtos = new ArrayList<>();
        for (Parcel pkg : truck.getParcels()) {
            parcelDtos.add(convertToPackageDto(pkg));
        }
        truckDto.setPackages(parcelDtos);

        return truckDto;
    }

    private ParcelDto convertToPackageDto(Parcel pkg) {
        ParcelDto parcelDto = new ParcelDto();
        parcelDto.setName(pkg.getName());
        parcelDto.setShape(pkg.getUniqueShape());
        parcelDto.setSymbol(pkg.getSymbol());

        ParcelStartPosition position = pkg.getParcelStartPosition();
        if (position != null) {
            ParcelDto.PositionDto positionDto = new ParcelDto.PositionDto();
            positionDto.setX(position.getX() + POSITION_OFFSET);
            positionDto.setY(position.getY() + POSITION_OFFSET);
            parcelDto.setStartPosition(positionDto);
        } else {
            log.warn("У посылки с именем '{}' отсутствует стартовая позиция", pkg.getName());
        }

        return parcelDto;
    }

    private File createFile() {
        File outputDir = new File(OUTPUT_DIRECTORY);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            log.error("Не удалось создать папку для файла Json");
            throw new RuntimeException("Не удалось создать папку для файла Json");
        }
        return new File(outputDir, FILE_NAME);
    }
}
