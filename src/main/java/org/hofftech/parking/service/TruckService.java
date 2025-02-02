package org.hofftech.parking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.InsufficientTrucksException;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для управления грузовиками и размещения посылок.
 * <p>
 * Предоставляет методы для распределения посылок по нескольким грузовикам,
 * равномерного распределения, создания грузовиков, а также для печати
 * состояния грузовиков.
 * </p>
 * Логирование осуществляется с помощью аннотации {@code @Slf4j}.
 * Конструктор с параметрами генерируется с помощью {@code @RequiredArgsConstructor},
 * что обеспечивает внедрение необходимых зависимостей.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TruckService {
    private static final String TRUCK_SIZE_SPLITTER = "x";

    private static final int WIDTH_INDEX = 0;
    private static final int HEIGHT_INDEX = 1;
    private static final int EXPECTED_SIZE_PARTS = 2;

    private static final String INVALID_TRUCK_SIZE_FORMAT_MESSAGE = "Размер грузовика должен быть в формате ширинаxвысота, например 10x10.";
    private static final String NON_NUMERIC_TRUCK_SIZE_MESSAGE = "Размеры грузовика должны быть числами.";
    private static final String NEGATIVE_TRUCK_SIZE_MESSAGE = "Размеры грузовика должны быть положительными числами.";

    private static final String STANDARD_TRUCK_SIZE = "10x10";
    private static final int NEXT_TRUCK_OFFSET = 1;

    private final ParcelService parcelService;
    private final FormatterService formatterService;

    /**
     * Добавляет посылки в несколько грузовиков.
     *
     * <p>
     * Размещает список посылок в предоставленных грузовиках.
     * При необходимости используется равномерный алгоритм распределения.
     * </p>
     *
     * @param parcelList        список посылок для размещения
     * @param isEvenAlgorithm   флаг, указывающий использовать ли равномерный алгоритм
     * @param trucksFromArgs    список размеров грузовиков, предоставленных через аргументы
     * @return список грузовиков с размещенными посылками
     * @throws InsufficientTrucksException если список грузовиков пуст и стандартные размеры не заданы
     * @throws InsufficientTrucksException если недостаточно грузовиков для размещения всех посылок
     */
    public List<Truck> addParcelsToMultipleTrucks(List<Parcel> parcelList, Boolean isEvenAlgorithm, List<String> trucksFromArgs) {
        log.info("Начало размещения упаковок. Всего упаковок: {}", parcelList.size());

        parcelList.sort(null);
        log.info("Упаковки отсортированы по высоте и ширине.");

        List<Truck> trucks = new ArrayList<>();

        if (trucksFromArgs.isEmpty()) {
            handleEmptyTrucksList(trucks, parcelList, isEvenAlgorithm);
            return trucks;
        }

        for (String providedTruckSize : trucksFromArgs) {
            trucks.add(createTruck(providedTruckSize));
        }

        if (Boolean.TRUE.equals(isEvenAlgorithm)) {
            distributeParcelsEvenly(parcelList, trucks);
        } else {
            placeParcels(parcelList, trucks);
        }

        log.info("Размещение завершено. Всего грузовиков: {}", trucks.size());
        return trucks;
    }

    /**
     * Обрабатывает случай, когда список грузовиков пуст.
     *
     * <p>
     * Создает стандартный грузовик, добавляет его в список грузовиков и размещает посылки.
     * </p>
     *
     * @param trucks             список грузовиков для добавления
     * @param parcelList         список посылок для размещения
     * @param isEvenAlgorithm    флаг, указывающий использовать ли равномерный алгоритм
     * @throws InsufficientTrucksException если стандартный размер грузовика не задан
     * @throws InsufficientTrucksException если размещение посылок невозможно
     */
    private void handleEmptyTrucksList(List<Truck> trucks, List<Parcel> parcelList, Boolean isEvenAlgorithm) {
        log.info("Массив грузовиков пуст. Используем стандартные размеры {} и плотное размещение.", STANDARD_TRUCK_SIZE);
        Truck standardTruck = createTruck(STANDARD_TRUCK_SIZE);
        trucks.add(standardTruck);

        if (isEvenAlgorithm) {
            distributeParcelsEvenly(parcelList, trucks);
        } else {
            placeParcels(parcelList, trucks);
        }

        log.warn("Аргумент с грузовиками пуст, погрузка выполнена с использованием стандартных грузовиков.");
    }

    /**
     * Равномерно распределяет посылки по грузовикам.
     *
     * <p>
     * Посылки распределяются по всем доступным грузовикам последовательно,
     * обеспечивая равномерное распределение нагрузки.
     * </p>
     *
     * @param parcels список посылок для распределения
     * @param trucks  список грузовиков, в которые будут размещены посылки
     * @throws InsufficientTrucksException если список грузовиков пуст
     * @throws InsufficientTrucksException если недостаточно грузовиков для размещения всех посылок
     */
    public void distributeParcelsEvenly(List<Parcel> parcels, List<Truck> trucks) {
        if (trucks.isEmpty()) {
            throw new InsufficientTrucksException("Невозможно распределить посылки: нет грузовиков.");
        }
        int totalParcels = parcels.size();
        int numberOfTrucks = trucks.size();
        int currentTruckIndex = 0;

        log.info("Распределяем {} посылок на {} грузовиков.", totalParcels, numberOfTrucks);

        for (Parcel nextParcel : parcels) {
            boolean isPlaced = false;
            for (int i = 0; i < numberOfTrucks; i++) {
                int truckIndex = (currentTruckIndex + i) % numberOfTrucks;
                Truck currentTruck = trucks.get(truckIndex);
                if (parcelService.tryPack(currentTruck, nextParcel)) {
                    log.info("Посылка {} успешно размещена в грузовике {}.", nextParcel.getName(), truckIndex + 1);
                    isPlaced = true;
                    currentTruckIndex = (truckIndex + NEXT_TRUCK_OFFSET) % numberOfTrucks;
                    break;
                }
            }
            if (!isPlaced) {
                throw new InsufficientTrucksException("Не хватает указанных грузовиков для размещения всех посылок!");
            }
        }
        log.info("Все посылки успешно распределены по грузовикам.");
    }

    /**
     * Размещает посылки в грузовиках по порядку.
     *
     * <p>
     * Для каждой посылки пытается найти подходящий грузовик и разместить её.
     * Если посылка не помещается, предпринимается повторная попытка размещения.
     * </p>
     *
     * @param parcelList список посылок для размещения
     * @param trucks     список грузовиков, в которые будут размещены посылки
     * @throws InsufficientTrucksException если недостаточно грузовиков для размещения всех посылок
     */
    private void placeParcels(List<Parcel> parcelList, List<Truck> trucks) {
        for (Parcel providedParcel : parcelList) {
            log.info("Пытаемся разместить упаковку с ID {} и именем {}.", providedParcel.getName(), providedParcel.getName());
            boolean isPlaced = attemptToPlaceParcel(providedParcel, trucks);

            if (!isPlaced) {
                log.info("Упаковка с ID {} не поместилась. Повторная попытка размещения в существующих грузовиках...", providedParcel.getName());
                isPlaced = retryToPlaceParcel(providedParcel, trucks);
            }

            if (!isPlaced) {
                throw new InsufficientTrucksException("Не хватает указанных грузовиков для размещения всех посылок!");
            }
        }
    }

    /**
     * Пытается разместить посылку в одном из грузовиков.
     *
     * @param parcel посылка для размещения
     * @param trucks список грузовиков
     * @return true если размещение удалось, иначе false
     */
    private boolean attemptToPlaceParcel(Parcel parcel, List<Truck> trucks) {
        for (Truck truck : trucks) {
            if (parcelService.tryPack(truck, parcel)) {
                log.info("Упаковка с ID {} успешно размещена в существующем грузовике.", parcel.getName());
                return true;
            }
        }
        return false;
    }

    /**
     * Пытается повторно разместить посылку в одном из грузовиков.
     *
     * @param parcel посылка для размещения
     * @param trucks список грузовиков
     * @return true если размещение удалось, иначе false
     */
    private boolean retryToPlaceParcel(Parcel parcel, List<Truck> trucks) {
        for (Truck truck : trucks) {
            if (parcelService.tryPack(truck, parcel)) {
                log.info("Упаковка с ID {} размещена после повторной проверки.", parcel.getName());
                return true;
            }
        }
        return false;
    }

    /**
     * Создает новый грузовик на основе предоставленного размера.
     *
     * <p>
     * Размеры грузовика передаются в виде строки, разделенной символом {@code TRUCK_SIZE_SPLITTER}.
     * </p>
     *
     * @param providedTruckSize строка, содержащая размеры грузовика в формате {@code ширинаxвысота}
     * @return новый экземпляр {@link Truck} с указанными размерами
     * @throws IllegalArgumentException если формат размера грузовика некорректен или размеры не положительные
     * @throws NumberFormatException    если размеры грузовика не являются числами
     */
    private Truck createTruck(String providedTruckSize) {
        String[] splitSize = providedTruckSize.split(TRUCK_SIZE_SPLITTER);

        if (splitSize.length != EXPECTED_SIZE_PARTS) {
            throw new IllegalArgumentException(INVALID_TRUCK_SIZE_FORMAT_MESSAGE);
        }

        try {
            int width = Integer.parseInt(splitSize[WIDTH_INDEX].trim());
            int height = Integer.parseInt(splitSize[HEIGHT_INDEX].trim());

            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException(NEGATIVE_TRUCK_SIZE_MESSAGE);
            }

            Truck currentTruck = new Truck(width, height);
            log.info("Создан грузовик размером {}x{}.", width, height);
            return currentTruck;
        } catch (NumberFormatException e) {
            throw new NumberFormatException(NON_NUMERIC_TRUCK_SIZE_MESSAGE);
        }
    }

    /**
     * Формирует строковое представление состояния всех грузовиков.
     *
     * <p>
     * Каждому грузовику присваивается номер, указывается его размер и отображается
     * текущая загруженность в виде текстовой сетки.
     * </p>
     *
     * @param trucks список грузовиков для отображения
     * @return строковое представление состояния всех грузовиков
     */
    public String printTrucks(List<Truck> trucks) {
        log.info("Начало формирования состояния всех грузовиков. Всего грузовиков: {}", trucks.size());
        StringBuilder result = new StringBuilder();
        int truckNumber = 1;

        for (Truck truck : trucks) {
            result.append("Truck ").append(truckNumber).append("\n")
                    .append(truck.getWidth()).append("x").append(truck.getHeight()).append("\n");
            result.append(formatterService.getTruckRepresentation(truck)).append("\n");
            truckNumber++;
        }

        return result.toString();
    }

    /**
     * Добавляет посылки в индивидуальные грузовики.
     *
     * <p>
     * Каждой посылке назначается отдельный грузовик на основе предоставленных размеров.
     * </p>
     *
     * @param parcels        список посылок для размещения
     * @param providedTrucks список размеров грузовиков в формате {@code ширинаxвысота}
     * @return список грузовиков с размещенными посылками
     * @throws InsufficientTrucksException если недостаточно предоставленных грузовиков для размещения всех посылок
     */
    public List<Truck> addParcelsToIndividualTrucks(List<Parcel> parcels, List<String> providedTrucks) {
        List<Truck> trucks = new ArrayList<>();
        int truckIndex = 0;

        for (Parcel parcel : parcels) {
            if (truckIndex >= providedTrucks.size()) {
                throw new InsufficientTrucksException("Не хватает предоставленных грузовиков для размещения всех посылок.");
            }
            Truck truck = createTruck(providedTrucks.get(truckIndex));
            truckIndex++;
            parcelService.tryPack(truck, parcel);
            trucks.add(truck);
        }
        return trucks;
    }
}
