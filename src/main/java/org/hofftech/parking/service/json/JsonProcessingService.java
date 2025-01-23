package org.hofftech.parking.service.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.InputFileException;
import org.hofftech.parking.exception.JsonWriteException;
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

/**
 * Сервис для обработки JSON данных, связанных с грузовиками и посылками.
 * Предоставляет методы для сохранения данных о грузовиках в JSON файл и импорта посылок из JSON.
 */
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

    /**
     * Сохраняет список грузовиков в JSON файл.
     *
     * @param trucks список грузовиков для сохранения
     * @return строковое представление JSON
     */
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
            throw new JsonWriteException("Не удалось записать JSON", e);
        }
    }

    /**
     * Получает начальную позицию посылки из DTO.
     *
     * @param parcelDto DTO посылки
     * @param position  текущая позиция
     * @return начальная позиция посылки
     */
    private ParcelStartPosition getParcelStartPosition(ParcelDto parcelDto, ParcelStartPosition position) {
        if (parcelDto.getStartPosition() != null) {
            position = new ParcelStartPosition(
                    parcelDto.getStartPosition().getX(),
                    parcelDto.getStartPosition().getY()
            );
        }
        return position;
    }

    /**
     * Импортирует посылки из JSON файла.
     *
     * @param jsonFilePath путь к JSON файлу
     * @param isWithCount  флаг группировки посылок с подсчетом
     * @param user         идентификатор пользователя
     * @return список карт с названиями посылок и их количеством
     */
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
            throw new RuntimeException("Ошибка маппинга: " + e.getMessage(), e);
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

    /**
     * Добавляет заказ на разгрузку.
     *
     * @param trucks  список DTO грузовиков
     * @param parcels список посылок
     * @param userId  идентификатор пользователя
     */
    public void addUnloadOrder(List<TruckDto> trucks, List<Parcel> parcels, String userId) {
        Order order = new Order(
                userId,
                LocalDate.now(),
                OrderOperationType.UNLOAD,
                trucks.size(),
                parcels
        );

        orderManagerService.addOrder(order);
        log.info("Добавлен заказ на разгрузку для {}", userId);
    }

    /**
     * Получает индивидуальные посылки без группировки.
     *
     * @param parcels список посылок
     * @return список карт с названиями посылок и их количеством
     */
    private List<Map<String, Long>> getIndividualParcels(List<Parcel> parcels) {
        return parcels.stream()
                .flatMap(parcel -> parcel.getName().repeat(1).lines())
                .map(name -> Map.of(name, 1L))
                .toList();
    }

    /**
     * Группирует посылки с подсчетом количества каждой.
     *
     * @param parcels список посылок
     * @return список карт с названиями посылок и их количеством
     */
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

    /**
     * Извлекает посылки из DTO грузовика и добавляет их в список.
     *
     * @param truck   DTO грузовика
     * @param parcels список для добавления посылок
     */
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

    /**
     * Преобразует объект Truck в TruckDto.
     *
     * @param truck      объект Truck
     * @param truckIndex индекс грузовика
     * @return объект TruckDto
     */
    private TruckDto convertToTruckDto(Truck truck, int truckIndex) {
        List<ParcelDto> parcelDtos = new ArrayList<>();
        for (Parcel parcel : truck.getParcels()) {
            parcelDtos.add(convertToParcelDto(parcel));
        }
        return new TruckDto(truckIndex + TRUCK_NAME_INDEX, truck.getWidth() + TRUCK_SIZE_SPLITTER + truck.getHeight(), parcelDtos);
    }

    /**
     * Преобразует объект Parcel в ParcelDto.
     *
     * @param providedParcel объект Parcel
     * @return объект ParcelDto
     */
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

    /**
     * Создает файл для сохранения JSON данных.
     *
     * @return объект File
     */
    private File createFile() {
        File outputDir = new File(OUTPUT_DIRECTORY);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("Не удалось создать папку для файла Json");
        }
        return new File(outputDir, FILE_NAME);
    }
}
