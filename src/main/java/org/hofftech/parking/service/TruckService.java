package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.entity.Truck;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.utill.TruckFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TruckService {
    private final ParcelService parcelService;
    private final TruckFactory truckFactory;

    public TruckService(ParcelService parcelService, TruckFactory truckFactory) {
        this.parcelService = parcelService;
        this.truckFactory = truckFactory;
    }

    public List<Truck> addParcelsToMultipleTrucks(List<ParcelDto> parcelDtoList, int maxTrucks, Boolean evenAlg) {
        log.info("Начало размещения упаковок. Всего упаковок: {}", parcelDtoList.size());

        sortParcels(parcelDtoList);
        List<Truck> truckEntities;

        if (!evenAlg) {
            truckEntities = truckFactory.createTrucks(1);
            placeParcels(parcelDtoList, truckEntities, maxTrucks);
        } else {
            truckEntities = truckFactory.createTrucks(maxTrucks);
            distributeParcelsEvenly(parcelDtoList, truckEntities);
        }

        log.info("Посылки размещены, количество грузовиков: {}", truckEntities.size());
        return truckEntities;
    }

    public void distributeParcelsEvenly(List<ParcelDto> parcelDtos, List<Truck> truckEntities) {
        if (truckEntities.isEmpty()) {
            log.error("Количество грузовиков не может быть равно 0.");
            throw new IllegalArgumentException("Невозможно распределить посылки: нет грузовиков.");
        }

        int totalParcels = parcelDtos.size();
        int numberOfTrucks = truckEntities.size();
        int minParcelsPerTruck = totalParcels / numberOfTrucks;
        int extraParcels = totalParcels % numberOfTrucks;

        log.info("Распределяем {} посылок на {} грузовиков. Минимум в грузовике: {}, дополнительные посылки: {}",
                totalParcels, numberOfTrucks, minParcelsPerTruck, extraParcels);

        List<List<ParcelDto>> truckParcels = new ArrayList<>();
        for (int i = 0; i < numberOfTrucks; i++) {
            truckParcels.add(new ArrayList<>());
        }

        int currentTruckIndex = 0;
        for (ParcelDto pkg : parcelDtos) {
            truckParcels.get(currentTruckIndex).add(pkg);
            currentTruckIndex = (currentTruckIndex + 1) % numberOfTrucks;
        }

        for (int i = 0; i < numberOfTrucks; i++) {
            Truck truck = truckEntities.get(i);
            List<ParcelDto> group = truckParcels.get(i);

            log.info("Заполняем грузовик {} из пачки {} посылок.", i + 1, group.size());
            for (ParcelDto pkg : group) {
                if (!parcelService.addParcels(truck, pkg)) {
                    log.error("Не получилось определить посылку {} в грузовик {}.", pkg.getType(), i + 1);
                    throw new RuntimeException("Не хватает кол-ва грузовиков для определения!");
                }
            }
        }

        log.info("Посылки распределены по грузовикам.");
    }

    private void placeParcels(List<ParcelDto> parcelDtoList, List<Truck> truckEntities, int maxTrucks) {
        for (ParcelDto pkg : parcelDtoList) {
            log.info("Пытаемся разместить упаковку {} с ID {}", pkg.getType(), pkg.getId());
            boolean placed = false;
            for (Truck truck : truckEntities) {
                if (parcelService.addParcels(truck, pkg)) {
                    log.info("Упаковка {} с ID {} успешно размещена в существующем грузовике.", pkg.getType(), pkg.getId());
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                log.info("Упаковка {} с ID {} не поместилась. Создаём новый грузовик.", pkg.getType(), pkg.getId());
                if (truckEntities.size() < maxTrucks) {
                    Truck newTruck = new Truck();
                    if (parcelService.addParcels(newTruck, pkg)) {
                        truckEntities.add(newTruck);
                        log.info("Упаковка {} с ID {} размещена в новом грузовике.", pkg.getType(), pkg.getId());
                    } else {
                        log.error("Ошибка: упаковка {} с ID {} не может быть размещена даже в новом грузовике.", pkg.getType(), pkg.getId());
                    }
                } else {
                    log.error("Превышен установленный лимит грузовиков!");
                    throw new RuntimeException("Превышен лимит грузовиков: " + maxTrucks);
                }
            }
        }
    }

    private static void sortParcels(List<ParcelDto> parcelDtoList) {
        parcelDtoList.sort(TruckService::compareParcels);
        log.info("Упаковки отсортированы по высоте и ширине.");
    }

    private static int compareParcels(ParcelDto a, ParcelDto b) {
        int heightDiff = Integer.compare(b.getType().getHeight(), a.getType().getHeight());
        if (heightDiff == 0) {
            return Integer.compare(b.getType().getWidth(), a.getType().getWidth());
        }
        return heightDiff;
    }

    public void printTrucks(List<Truck> truckEntities) {
        log.info("Всего грузовиков: {}", truckEntities.size());
        int truckNumber = 1;
        for (Truck truck : truckEntities) {
            System.out.printf("Truck %d%n", truckNumber);
            printTruck(truck);
            truckNumber++;
        }
        log.info("Вывод завершён-------------");
    }

    private void printTruck(Truck truck) {
        for (int y = truck.getHEIGHT() - 1; y >= 0; y--) {
            System.out.print("+");
            for (int x = 0; x < truck.getWIDTH(); x++) {
                System.out.print(truck.getGrid()[y][x]);
            }
            System.out.println("+");
        }
        System.out.println("++++++++" + "\n");
    }

    public List<Truck> addParcelsToIndividualTrucks(List<ParcelDto> parcelDtos) {
        List<Truck> truckEntities = new ArrayList<>();
        for (ParcelDto pkg : parcelDtos) {
            Truck truck = new Truck();
            parcelService.addParcels(truck, pkg);
            truckEntities.add(truck);
            log.info("Упаковка {} добавлена в новый грузовик.", pkg.getId());
        }
        return truckEntities;
    }
}

