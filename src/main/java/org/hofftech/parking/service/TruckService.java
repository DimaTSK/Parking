package org.hofftech.parking.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TruckService {
    private static final String TRUCK_STANDARD_SIZE = "6x6";
    private static final String TRUCK_SIZE_DELIMITER = "x";
    private static final int DEFAULT_TRUCK_WIDTH = 6;
    private static final int DEFAULT_TRUCK_HEIGHT = 6;

    private final ParcelService parcelService;

    public List<Truck> addPackagesToMultipleTrucks(List<Parcel> parcelList, Boolean evenAlg, List<String> trucksFromArgs) {
        log.info("Начало размещения упаковок. Всего упаковок: {}", parcelList.size());
        sortPackages(parcelList);

        List<Truck> trucks = new ArrayList<>();

        if (trucksFromArgs.isEmpty()) {
            log.info("Массив грузовиков пуст. Используем стандартные размеры {} и плотное размещение.", TRUCK_STANDARD_SIZE);
            placePackagesInStandardTrucks(parcelList, trucks);
        } else {
            for (String providedTruckSize : trucksFromArgs) {
                trucks.add(createTruck(providedTruckSize));
            }
            if (!evenAlg) {
                placePackages(parcelList, trucks);
            } else {
                distributePackagesEvenly(parcelList, trucks);
            }
        }

        log.info("Размещение завершено. Всего грузовиков: {}", trucks.size());
        return trucks;
    }

    private void placePackagesInStandardTrucks(List<Parcel> parcelList, List<Truck> trucks) {
        while (!parcelList.isEmpty()) {
            Truck truck = createTruck(TRUCK_STANDARD_SIZE);
            Iterator<Parcel> iterator = parcelList.iterator();

            while (iterator.hasNext()) {
                Parcel pkg = iterator.next();
                if (parcelService.addPackage(truck, pkg)) {
                    iterator.remove();
                    log.info("Упаковка {} успешно размещена в грузовике с размерами {}.", pkg.getName(), TRUCK_STANDARD_SIZE);
                } else {
                    log.info("Упаковка {} не помещается в текущий грузовик. Создаем новый.", pkg.getName());
                    break; // Переходим к созданию нового грузовика
                }
            }
            trucks.add(truck);
        }
    }

    public void distributePackagesEvenly(List<Parcel> parcels, List<Truck> trucks) {
        if (trucks.isEmpty()) {
            log.error("Количество грузовиков не может быть нулевым.");
            throw new IllegalArgumentException("Невозможно распределить посылки: нет грузовиков.");
        }
        int totalPackages = parcels.size();
        int numberOfTrucks = trucks.size();
        int currentTruckIndex = 0;

        log.info("Распределяем {} посылок на {} грузовиков.", totalPackages, numberOfTrucks);

        for (Parcel pkg : parcels) {
            boolean placed = false;
            for (int i = 0; i < numberOfTrucks; i++) {
                Truck currentTruck = trucks.get((currentTruckIndex + i) % numberOfTrucks);
                if (parcelService.addPackage(currentTruck, pkg)) {
                    log.info("Посылка {} успешно размещена в грузовике {}.", pkg.getName(), (currentTruckIndex + i) % numberOfTrucks + 1);
                    placed = true;
                    currentTruckIndex = (currentTruckIndex + i + 1) % numberOfTrucks;
                    break;
                }
            }
            if (!placed) {
                log.error("Не удалось разместить посылку {} в любом из грузовиков.", pkg.getName());
                throw new RuntimeException("Не хватает указанных грузовиков для размещения!");
            }
        }
        log.info("Все посылки успешно распределены по грузовикам.");
    }

    private void placePackages(List<Parcel> parcelList, List<Truck> trucks) {
        int nextTruckIndex = trucks.size();
        for (Parcel pkg : parcelList) {
            log.info("Пытаемся разместить упаковку с ID {} и именем {}.", pkg.getName(), pkg.getName());
            boolean placed = tryPlacePackageInExistingTrucks(pkg, trucks);

            if (!placed) {
                placed = tryPlacePackageWithRetry(pkg, trucks);
            }

            if (!placed) {
                createAndPlaceInNewTruck(pkg, trucks, nextTruckIndex);
                nextTruckIndex++;
            }
        }
    }

    private boolean tryPlacePackageInExistingTrucks(Parcel pkg, List<Truck> trucks) {
        for (Truck truck : trucks) {
            if (parcelService.addPackage(truck, pkg)) {
                log.info("Упаковка с ID {} успешно размещена в существующем грузовике.", pkg.getName());
                return true;
            }
        }
        return false;
    }

    private boolean tryPlacePackageWithRetry(Parcel pkg, List<Truck> trucks) {
        log.info("Упаковка с ID {} не поместилась. Повторная попытка размещения в существующих грузовиках...", pkg.getName());

        for (Truck truck : trucks) {
            if (parcelService.addPackage(truck, pkg)) {
                log.info("Упаковка с ID {} размещена после повторной проверки.", pkg.getName());
                return true;
            }
        }
        return false;
    }

    private void createAndPlaceInNewTruck(Parcel pkg, List<Truck> trucks, int truckIndex) {
        Truck newTruck;
        if (truckIndex < trucks.size()) {
            newTruck = trucks.get(truckIndex);
        } else {
            newTruck = createTruck(TRUCK_STANDARD_SIZE);
            trucks.add(newTruck);
        }

        if (parcelService.addPackage(newTruck, pkg)) {
            log.info("Упаковка с ID {} размещена в новом грузовике.", pkg.getName());
        } else {
            log.error("Ошибка: упаковка с ID {} не может быть размещена даже в новом грузовике.", pkg.getName());
            throw new RuntimeException("Все доступные грузовики использованы, упаковка не может быть размещена.");
        }
    }

    private Truck createTruck(String providedTruckSize) {
        if (providedTruckSize == null || providedTruckSize.isEmpty()) {
            providedTruckSize = TRUCK_STANDARD_SIZE;
        }
        String[] splitSize = providedTruckSize.split(TRUCK_SIZE_DELIMITER); // Используем константу
        if (splitSize.length != 2) {
            log.error("Неверный формат размера грузовика: {}", providedTruckSize);
            throw new IllegalArgumentException("Размер грузовика должен быть в формате 'ширинаxвысота'.");
        }

        int width;
        int height;
        try {
            width = Integer.parseInt(splitSize[0].trim());
            height = Integer.parseInt(splitSize[1].trim());
        } catch (NumberFormatException e) {
            log.error("Размеры грузовика должны быть целыми числами. Получено: {}", providedTruckSize);
            throw new IllegalArgumentException("Размеры грузовика должны быть целыми числами.", e);
        }

        Truck currentTruck = new Truck(width, height);
        log.info("Создан грузовик размером {}x{}.", width, height);
        return currentTruck;
    }

    private void sortPackages(List<Parcel> parcelList) {
        parcelList.sort((a, b) -> {
            int heightDiff = Integer.compare(b.getHeight(), a.getHeight());
            if (heightDiff == 0) {
                return Integer.compare(b.getWidth(), a.getWidth());
            }
            return heightDiff;
        });
        log.info("Упаковки отсортированы по высоте и ширине.");
    }

    /**
     * Печатает состояние всех грузовиков.
     *
     * @param trucks список грузовиков
     */
    @SneakyThrows
    public void printTrucks(List<Truck> trucks) {
        log.info("Начало вывода состояния всех грузовиков. Всего грузовиков: {}", trucks.size());
        int truckNumber = 1;
        for (Truck truck : trucks) {
            System.out.println("Truck " + truckNumber + "\n" + truck.getWidth() + "x" + truck.getHeight());
            printTruck(truck);
            truckNumber++;
        }
    }

    private void printTruck(Truck truck) {
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
        System.out.println(truckRepresentation.toString());
    }

    public List<Truck> addPackagesToIndividualTrucks(List<Parcel> parcels, List<String> providedTrucks) {
        List<Truck> trucks = new ArrayList<>();
        int truckIndex = 0;

        for (Parcel pkg : parcels) {
            if (truckIndex >= providedTrucks.size()) {
                log.info("Массив переданных грузовиков пуст или недостаточно грузовиков. Будет создан стандартный грузовик");
                providedTrucks.add(TRUCK_STANDARD_SIZE);
            }
            Truck truck = createTruck(providedTrucks.get(truckIndex));
            truckIndex++;
            if (!parcelService.addPackage(truck, pkg)) {
                log.error("Упаковка с ID {} не может быть размещена в грузовике.", pkg.getName());
                throw new RuntimeException("Не удалось разместить упаковку в грузовике.");
            }
            trucks.add(truck);
        }
        return trucks;
    }
}
