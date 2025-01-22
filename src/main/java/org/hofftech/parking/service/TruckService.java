package org.hofftech.parking.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.Truck;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TruckService {

    private static final String TRUCK_SIZE_SPLITTER = "x";
    private static final int FIRST_ROW_INDEX = 0;
    private static final int FIRST_PART = 0;
    private static final int SECOND_PART = 1;
    private final ParcelService parcelService;

    public List<Truck> addParcelsToMultipleTrucks(List<Parcel> parcelList, Boolean isEvenAlgorithm, List<String> trucksFromArgs) {
        log.info("Начало размещения упаковок. Всего упаковок: {}", parcelList.size());
        parcelList.sort(null);
        log.info("Упаковки отсортированы по высоте и ширине.");

        List<Truck> trucks = new ArrayList<>();

        if (trucksFromArgs.isEmpty()) {
            throw new IllegalArgumentException("Аргумент с грузовиками пуст, погрузка невозможна");
        } else {
            for (String providedTruckSize : trucksFromArgs) {
                trucks.add(createTruck(providedTruckSize));
            }
            if (!isEvenAlgorithm) {
                placeParcels(parcelList, trucks);
            } else {
                distributeParcelsEvenly(parcelList, trucks);
            }
        }

        log.info("Размещение завершено. Всего грузовиков: {}", trucks.size());
        return trucks;
    }

    public void distributeParcelsEvenly(List<Parcel> parcels, List<Truck> trucks) {
        if (trucks.isEmpty()) {
            throw new IllegalArgumentException("Невозможно распределить посылки: нет грузовиков.");
        }
        int totalParcels = parcels.size();
        int numberOfTrucks = trucks.size();
        int currentTruckIndex = 0;

        log.info("Распределяем {} посылок на {} грузовиков.", totalParcels, numberOfTrucks);

        for (Parcel nextParcel : parcels) {
            boolean isPlaced = false;
            for (int i = FIRST_ROW_INDEX; i < numberOfTrucks; i++) {
                Truck currentTruck = trucks.get((currentTruckIndex + i) % numberOfTrucks);
                if (parcelService.tryPack(currentTruck, nextParcel)) {
                    log.info("Посылка {} успешно размещена в грузовике {}.", nextParcel.getName(), (currentTruckIndex + i) % numberOfTrucks + 1);
                    isPlaced = true;
                    currentTruckIndex = (currentTruckIndex + i + 1) % numberOfTrucks;
                    break;
                }
            }
            if (!isPlaced) {
                throw new RuntimeException("Не хватает указанных грузовиков для размещения!");
            }
        }
        log.info("Все посылки успешно распределены по грузовикам.");
    }


    private void placeParcels(List<Parcel> parcelList, List<Truck> trucks) {
        for (Parcel providedParcel : parcelList) {
            log.info("Пытаемся разместить упаковку с ID {} и именем {}.", providedParcel.getName(), providedParcel.getName());
            boolean isPlaced = false;

            for (Truck truck : trucks) {
                if (parcelService.tryPack(truck, providedParcel)) {
                    log.info("Упаковка с ID {} успешно размещена в существующем грузовике.", providedParcel.getName());
                    isPlaced = true;
                    break;
                }
            }
            if (!isPlaced) {
                log.info("Упаковка с ID {} не поместилась. Повторная попытка размещения в существующих грузовиках...", providedParcel.getName());

                for (Truck truck : trucks) {
                    if (parcelService.tryPack(truck, providedParcel)) {
                        log.info("Упаковка с ID {} размещена после повторной проверки.", providedParcel.getName());
                        break;
                    }
                }
            }
        }
    }

    private Truck createTruck(String providedTruckSize) {
        String[] splitSize = providedTruckSize.split(TRUCK_SIZE_SPLITTER);
        Truck currentTruck = new Truck(Integer.parseInt(splitSize[FIRST_PART].trim()), Integer.parseInt(splitSize[SECOND_PART].trim()));
        log.info("Создан грузовик размером {}x{}.", splitSize[0].trim(), splitSize[1].trim());
        return currentTruck;
    }

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

    public List<Truck> addParcelsToIndividualTrucks(List<Parcel> parcels, List<String> providedTrucks) {
        List<Truck> trucks = new ArrayList<>();
        int truckIndex = 0;

        for (Parcel parcel : parcels) {
            Truck truck = createTruck(providedTrucks.get(truckIndex));
            truckIndex++;
            parcelService.tryPack(truck, parcel);
            trucks.add(truck);
        }
        return trucks;
    }
}