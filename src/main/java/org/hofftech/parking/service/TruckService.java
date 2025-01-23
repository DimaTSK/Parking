package org.hofftech.parking.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.InsufficientTrucksException;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;

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
public class TruckService {

    private static final String TRUCK_SIZE_SPLITTER = "x";
    private static final int FIRST_PART = 0;
    private static final int SECOND_PART = 1;
    private static final String EMPTY_TRUCKS_ERROR_MESSAGE = "Аргумент с грузовиками пуст, погрузка невозможна";
    private static final String STANDARD_TRUCK_SIZE = "10x10";
    private static final boolean DEFAULT_IS_EVEN_ALGORITHM = false;
    private static final int NEXT_TRUCK_OFFSET = 1;

    private final ParcelService parcelService;

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

        if (!isEvenAlgorithm) {
            placeParcels(parcelList, trucks);
        } else {
            distributeParcelsEvenly(parcelList, trucks);
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

        if (!isEvenAlgorithm) {
            placeParcels(parcelList, trucks);
        } else {
            distributeParcelsEvenly(parcelList, trucks);
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
                Truck currentTruck = trucks.get((currentTruckIndex + i) % numberOfTrucks);
                if (parcelService.tryPack(currentTruck, nextParcel)) {
                    log.info("Посылка {} успешно размещена в грузовике {}.", nextParcel.getName(), ((currentTruckIndex + i) % numberOfTrucks) + 1);
                    isPlaced = true;
                    currentTruckIndex = (currentTruckIndex + i + NEXT_TRUCK_OFFSET) % numberOfTrucks;
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
            logAttemptPlacement(providedParcel);
            boolean isPlaced = attemptToPlaceParcel(providedParcel, trucks);

            if (!isPlaced) {
                logRetryPlacement(providedParcel);
                isPlaced = retryToPlaceParcel(providedParcel, trucks);
            }

            if (!isPlaced) {
                handlePlacementFailure(providedParcel);
            }
        }
    }

    /**
     * Логирует попытку размещения посылки.
     *
     * @param parcel посылка для размещения
     */
    private void logAttemptPlacement(Parcel parcel) {
        log.info("Пытаемся разместить упаковку с ID {} и именем {}.", parcel.getName(), parcel.getName());
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
     * Логирует начало повторной попытки размещения посылки.
     *
     * @param parcel посылка для размещения
     */
    private void logRetryPlacement(Parcel parcel) {
        log.info("Упаковка с ID {} не поместилась. Повторная попытка размещения в существующих грузовиках...", parcel.getName());
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
     * Обрабатывает ситуацию, когда размещение посылки не удалось.
     *
     * @param parcel посылка, которую не удалось разместить
     */
    private void handlePlacementFailure(Parcel parcel) {
        log.error("Не удалось разместить посылку с ID {} в существующих грузовиках.", parcel.getName());
        throw new InsufficientTrucksException("Не хватает указанных грузовиков для размещения всех посылок!");
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
     * @throws NumberFormatException если размеры грузовика не являются числами
     */
    private Truck createTruck(String providedTruckSize) {
        String[] splitSize = providedTruckSize.split(TRUCK_SIZE_SPLITTER);
        if (splitSize.length != 2) {
            log.error("Некорректный формат размера грузовика: {}", providedTruckSize);
            throw new IllegalArgumentException("Размер грузовика должен быть в формате ширинаxвысота, например 10x10.");
        }
        try {
            int width = Integer.parseInt(splitSize[FIRST_PART].trim());
            int height = Integer.parseInt(splitSize[SECOND_PART].trim());
            Truck currentTruck = new Truck(width, height);
            log.info("Создан грузовик размером {}x{}.", width, height);
            return currentTruck;
        } catch (NumberFormatException e) {
            log.error("Не удалось преобразовать размеры грузовика в числа: {}", providedTruckSize);
            throw new NumberFormatException("Размеры грузовика должны быть числами.");
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
    @SneakyThrows
    public String printTrucks(List<Truck> trucks) {
        log.info("Начало формирования состояния всех грузовиков. Всего грузовиков: {}", trucks.size());
        StringBuilder result = new StringBuilder();
        int truckNumber = 1;

        for (Truck truck : trucks) {
            result.append("Truck ").append(truckNumber).append("\n")
                    .append(truck.getWidth()).append("x").append(truck.getHeight()).append("\n");
            result.append(getTruckRepresentation(truck)).append("\n");
            truckNumber++;
        }

        return result.toString();
    }

    /**
     * Возвращает строковое представление загруженности конкретного грузовика.
     *
     * <p>
     * Отображает содержимое грузовика в виде сетки с границами.
     * Пустые ячейки обозначаются пробелами.
     * </p>
     *
     * @param truck грузовик для отображения
     * @return строковое представление грузовика
     */
    private String getTruckRepresentation(Truck truck) {
        StringBuilder truckRepresentation = new StringBuilder();
        truckRepresentation.append("+").append("+".repeat(truck.getWidth())).append("+\n");

        for (int y = truck.getHeight() - 1; y >= 0; y--) {
            truckRepresentation.append("+");
            for (int x = 0; x < truck.getWidth(); x++) {
                char cell = truck.getGrid()[y][x];
                truckRepresentation.append(cell == '\0' ? ' ' : cell);
            }
            truckRepresentation.append("+\n");
        }
        truckRepresentation.append("+").append("+".repeat(truck.getWidth())).append("+\n");

        return truckRepresentation.toString();
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
                log.error("Недостаточно предоставленных грузовиков для размещения посылок.");
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