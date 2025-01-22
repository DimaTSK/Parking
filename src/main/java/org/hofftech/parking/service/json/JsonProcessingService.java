package org.hofftech.parking.service.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.InputFileException;
import org.hofftech.parking.model.Order;
import org.hofftech.parking.model.enums.OrderOperationType;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.PositionDto;
import org.hofftech.parking.model.dto.TruckDto;
import org.hofftech.parking.service.OrderManagerService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JsonProcessingService {
    private static final String OUTPUT_DIRECTORY = "out";
    private static final String FILE_NAME = "trucks.json";
    private static final String TRUCKS_ARRAY = "trucks";
    private static final int ADJUSTING_FOR_START_POSITION = 1;
    private static final int TRUCK_NAME_INDEX = 1;
    private static final String TRUCK_SIZE_SPLITTER = "x";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderManagerService orderManagerService;

    public JsonProcessingService(OrderManagerService orderManagerService) {
        this.orderManagerService = orderManagerService;
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String saveToJson(List<Truck> trucks) {
        File outputFile = createFile();
        List<TruckDto> trucksData = new ArrayList<>();
        for (int i = 0; i < trucks.size(); i++) {
            trucksData.add(convertToTruckDto(trucks.get(i), i));
        }

        try {
            String jsonString = objectMapper.writeValueAsString(Map.of("trucks", trucksData));
            objectMapper.writeValue(outputFile, Map.of("trucks", trucksData));
            log.info("JSON файл успешно создан: {}", outputFile.getAbsolutePath());
            return jsonString;
        } catch (IOException e) {
            log.error("Ошибка при записи JSON: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private ParcelStartPosition getParcelStartPosition(ParcelDto parcelDto, ParcelStartPosition position) {
        if (parcelDto.getStartPosition() != null) {
            position = new ParcelStartPosition(
                    parcelDto.getStartPosition().getX(),
                    parcelDto.getStartPosition().getY()
            );
        }
        return position;
    }

    @SneakyThrows
    public List<Map<String, Long>> importParcelsFromJson(String jsonFilePath, boolean isWithCount, String user) {
        File jsonFile = new File(jsonFilePath);
        if (!jsonFile.exists()) {
            throw new InputFileException("Файл не найден: " + jsonFilePath);
        }
        Map<String, List<TruckDto>> jsonData;
        try {
            jsonData = objectMapper.readValue(
                    jsonFile,
                    new TypeReference<>() {
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException("Ошибка маппинга: " + e.getMessage());
        }
        List<Parcel> parcels = new ArrayList<>();
        List<TruckDto> trucks = jsonData.get(TRUCKS_ARRAY);
        for (TruckDto truck : trucks) {
            extractParcelsFromTruck(truck, parcels);
        }

        addUnloadOrder(trucks, parcels, user);

        if (isWithCount) {
            return groupParcelsWithCount(parcels);
        } else {
            return getIndividualParcels(parcels);
        }
    }

    public void addUnloadOrder(List<TruckDto> trucks, List<Parcel> parcels, String userId) {
        Order order = new Order(
                userId,
                LocalDate.now(),
                OrderOperationType.UNLOAD,
                trucks.size(),
                parcels
        );

        orderManagerService.addOrder(order);
        log.info("Добавлен заказ на рагрузку для {}", userId);
    }

    private List<Map<String, Long>> getIndividualParcels(List<Parcel> parcels) {
        return parcels.stream()
                .flatMap(parcel -> parcel.getName().repeat(1).lines())
                .map(name -> Map.of(name, 1L))
                .toList();
    }

    private List<Map<String, Long>> groupParcelsWithCount(List<Parcel> parcels) {
        return parcels.stream()
                .collect(Collectors.groupingBy(
                        Parcel::getName,
                        LinkedHashMap::new,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(entry -> Map.of(entry.getKey(), entry.getValue()))
                .toList();
    }

    private void extractParcelsFromTruck(TruckDto truck, List<Parcel> parcels) {
        for (ParcelDto parcelDto : truck.getParcels()) {
            ParcelStartPosition position = null;
            position = getParcelStartPosition(parcelDto, position);
            Parcel parcel = new Parcel(
                    parcelDto.getName(),
                    parcelDto.getShape(),
                    parcelDto.getSymbol(),
                    position
            );
            parcels.add(parcel);
        }
    }

    private TruckDto convertToTruckDto(Truck truck, int truckIndex) {
        List<ParcelDto> parcelDtos = new ArrayList<>();
        for (Parcel parcel : truck.getParcels()) {
            parcelDtos.add(convertToParcelDto(parcel));
        }
        return new TruckDto(truckIndex + TRUCK_NAME_INDEX, truck.getWidth() + TRUCK_SIZE_SPLITTER + truck.getHeight(), parcelDtos);
    }

    private ParcelDto convertToParcelDto(Parcel providedParcel) {
        ParcelDto parcelDto = new ParcelDto();
        parcelDto.setName(providedParcel.getName());
        parcelDto.setShape(providedParcel.getReversedShape());
        parcelDto.setSymbol(providedParcel.getSymbol());

        ParcelStartPosition position = providedParcel.getParcelStartPosition();
        if (position != null) {
            PositionDto positionDto = new PositionDto();
            positionDto.setX(position.x() + ADJUSTING_FOR_START_POSITION);
            positionDto.setY(position.y() + ADJUSTING_FOR_START_POSITION);
            parcelDto.setStartPosition(positionDto);
        } else {
            throw new RuntimeException("Отсутствует стартовая позиция у посылки " + providedParcel.getName());
        }

        return parcelDto;
    }

    private File createFile() {
        File outputDir = new File(OUTPUT_DIRECTORY);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("Не удалось создать папку для файла Json");
        }
        return new File(outputDir, FILE_NAME);
    }
}