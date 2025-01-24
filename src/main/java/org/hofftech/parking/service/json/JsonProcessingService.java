package org.hofftech.parking.service.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.FileCreationException;
import org.hofftech.parking.exception.InputFileException;
import org.hofftech.parking.exception.JsonMappingException;
import org.hofftech.parking.exception.JsonWriteException;
import org.hofftech.parking.exception.MissingStartPositionException;
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
import java.util.*;
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

    private static final long INDIVIDUAL_PARCEL_QUANTITY = 1L;

    private final ObjectMapper objectMapper;
    private final OrderManagerService orderManagerService;

    public JsonProcessingService(OrderManagerService orderManagerService, ObjectMapper objectMapper) {
        this.orderManagerService = orderManagerService;
        this.objectMapper = objectMapper;
    }

    /**
     * Сохраняет список грузовиков в JSON файл.
     *
     * @param trucks список грузовиков для сохранения
     * @return строковое представление JSON
     */
    public String saveToJson(List<Truck> trucks) {
        File outputFile = createFile();
        List<TruckDto> trucksData = trucks.stream()
                .map(this::convertToTruckDto)
                .collect(Collectors.toList());

        try {
            Map<String, List<TruckDto>> data = Map.of(TRUCKS_ARRAY, trucksData);
            String jsonString = objectMapper.writeValueAsString(data);
            objectMapper.writeValue(outputFile, data);
            log.info("JSON файл успешно создан: {}", outputFile.getAbsolutePath());
            return jsonString;
        } catch (IOException e) {
            throw new JsonWriteException("Не удалось записать JSON", e);
        }
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
        File jsonFile = validateAndGetFile(jsonFilePath);
        Map<String, List<TruckDto>> jsonData = parseJsonFile(jsonFile);
        List<Parcel> parcels = extractAllParcels(jsonData.get(TRUCKS_ARRAY));
        addUnloadOrder(jsonData.get(TRUCKS_ARRAY), parcels, user);
        return isWithCount ? groupParcelsWithCount(parcels) : getIndividualParcels(parcels);
    }

    /**
     * Проверяет существование файла и возвращает объект File.
     *
     * @param jsonFilePath путь к JSON файлу
     * @return объект File
     */
    private File validateAndGetFile(String jsonFilePath) {
        File jsonFile = new File(jsonFilePath);
        if (!jsonFile.exists()) {
            throw new InputFileException("Файл не найден: " + jsonFilePath);
        }
        return jsonFile;
    }

    /**
     * Парсит JSON файл и возвращает структуру данных.
     *
     * @param jsonFile объект File
     * @return карта с данными из JSON
     */
    private Map<String, List<TruckDto>> parseJsonFile(File jsonFile) {
        try {
            return objectMapper.readValue(jsonFile, new TypeReference<>() {});
        } catch (IOException e) {
            throw new JsonMappingException("Ошибка маппинга JSON файла: " + e.getMessage(), e);
        }
    }

    /**
     * Извлекает все посылки из списка грузовиков.
     *
     * @param trucks список DTO грузовиков
     * @return список посылок
     */
    private List<Parcel> extractAllParcels(List<TruckDto> trucks) {
        List<Parcel> parcels = new ArrayList<>();
        for (TruckDto truck : trucks) {
            extractParcelsFromTruck(truck, parcels);
        }
        return parcels;
    }

    /**
     * Добавляет заказ на разгрузку.
     *
     * @param trucks  список DTO грузовиков
     * @param parcels список посылок
     * @param userId  идентификатор пользователя
     */
    private void addUnloadOrder(List<TruckDto> trucks, List<Parcel> parcels, String userId) {
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
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список индивидуальных посылок.
     *
     * @param parcels список посылок
     * @return список карт с названиями посылок и количеством 1
     */
    private List<Map<String, Long>> getIndividualParcels(List<Parcel> parcels) {
        return parcels.stream()
                .map(parcel -> Map.of(parcel.getName(), INDIVIDUAL_PARCEL_QUANTITY))
                .collect(Collectors.toList());
    }

    /**
     * Извлекает посылки из DTO грузовика и добавляет их в список.
     *
     * @param truck   DTO грузовика
     * @param parcels список для добавления посылок
     */
    private void extractParcelsFromTruck(TruckDto truck, List<Parcel> parcels) {
        for (ParcelDto parcelDto : truck.getParcels()) {
            ParcelStartPosition position = getParcelStartPosition(parcelDto);
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
     * Получает начальную позицию посылки из DTO.
     *
     * @param parcelDto DTO посылки
     * @return начальная позиция посылки
     */
    private ParcelStartPosition getParcelStartPosition(ParcelDto parcelDto) {
        if (parcelDto.getStartPosition() != null) {
            return new ParcelStartPosition(
                    parcelDto.getStartPosition().getX(),
                    parcelDto.getStartPosition().getY()
            );
        }
        throw new MissingStartPositionException("Отсутствует стартовая позиция у посылки " + parcelDto.getName());
    }

    /**
     * Преобразует объект Truck в TruckDto.
     *
     * @param truck объект Truck
     * @return объект TruckDto
     */
    private TruckDto convertToTruckDto(Truck truck) {
        List<ParcelDto> parcelDtos = truck.getParcels().stream()
                .map(this::convertToParcelDto)
                .collect(Collectors.toList());
        String size = truck.getWidth() + TRUCK_SIZE_SPLITTER + truck.getHeight();
        return new TruckDto(TRUCK_NAME_INDEX, size, parcelDtos);
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
            throw new MissingStartPositionException("Отсутствует стартовая позиция у посылки " + providedParcel.getName());
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
            throw new FileCreationException("Не удалось создать папку для файла Json");
        }
        return new File(outputDir, FILE_NAME);
    }
}
