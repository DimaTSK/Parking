package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.model.Package;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TruckService {
    private final PackingService packingService;

    public TruckService(PackingService packingService) {
        this.packingService = packingService;
    }

    public List<Truck> addPackagesToMultipleTrucks(List<Package> packageList, int maxTrucks, Boolean evenAlg) {
        log.info("Начало размещения упаковок. Всего упаковок: {}", packageList.size());

        sortPackages(packageList);
        List<Truck> trucks;

        if (!evenAlg) {
            trucks = createTruck(1);
            placePackages(packageList, trucks, maxTrucks);
        }
        else {
            trucks = createTruck(maxTrucks);
            distributePackagesEvenly(packageList, trucks);
        }

        log.info("Размещение завершено. Всего грузовиков: {}", trucks.size());
        return trucks;
    }


    public void distributePackagesEvenly(List<Package> packages, List<Truck> trucks) {
        if (trucks.isEmpty()) {
            log.error("Количество грузовиков не может быть нулевым.");
            throw new IllegalArgumentException("Невозможно распределить посылки: нет грузовиков.");
        }

        int totalPackages = packages.size();
        int numberOfTrucks = trucks.size();
        int minPackagesPerTruck = totalPackages / numberOfTrucks;
        int extraPackages = totalPackages % numberOfTrucks;

        log.info("Распределяем {} посылок на {} грузовиков. Минимум в грузовике: {}, дополнительные посылки: {}",
                totalPackages, numberOfTrucks, minPackagesPerTruck, extraPackages);

        List<List<Package>> truckPackages = new ArrayList<>();
        for (int i = 0; i < numberOfTrucks; i++) {
            truckPackages.add(new ArrayList<>());
        }

        int currentTruckIndex = 0;
        for (Package pkg : packages) {
            truckPackages.get(currentTruckIndex).add(pkg);
            currentTruckIndex = (currentTruckIndex + 1) % numberOfTrucks; // Переходим к следующему грузовику
        }

        for (int i = 0; i < numberOfTrucks; i++) {
            Truck truck = trucks.get(i);
            List<Package> group = truckPackages.get(i);

            log.info("Заполняем грузовик {} из группы {} посылок.", i + 1, group.size());
            for (Package pkg : group) {
                if (!packingService.addPackage(truck, pkg)) {
                    log.error("Не удалось разместить посылку {} в грузовик {}.", pkg.getType(), i + 1);
                    throw new RuntimeException("Не хватает указанного кол-ва грузовиков для размещения!");
                }
            }
        }

        log.info("Все посылки успешно распределены по грузовикам.");
    }



    private void placePackages(List<Package> packageList, List<Truck> trucks, int maxTrucks) {
        for (Package pkg : packageList) {
            log.info("Пытаемся разместить упаковку {} с ID {}", pkg.getType(), pkg.getId());
            boolean placed = false;
            for (Truck truck : trucks) {
                if (packingService.addPackage(truck, pkg)) {
                    log.info("Упаковка {} с ID {} успешно размещена в существующем грузовике.", pkg.getType(), pkg.getId());
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                log.info("Упаковка {} с ID {} не поместилась. Создаём новый грузовик.", pkg.getType(), pkg.getId());
                if (trucks.size() < maxTrucks) {
                    Truck newTruck = new Truck();
                    if (packingService.addPackage(newTruck, pkg)) {
                        trucks.add(newTruck);
                        log.info("Упаковка {} с ID {} размещена в новом грузовике.", pkg.getType(), pkg.getId());
                    } else {
                        log.error("Ошибка: упаковка {} с ID {} не может быть размещена даже в новом грузовике.", pkg.getType(), pkg.getId());
                    }
                }
                else {
                    log.error("Превышен установленный лимит грузовиков!");
                    throw new RuntimeException("Превышен лимит грузовиков: " + maxTrucks);
                }
            }
        }
    }

    private static List<Truck> createTruck(int countOfTrucks) {
        List<Truck> trucks = new ArrayList<>();
        Truck currentTruck = new Truck();
        trucks.add(currentTruck);
        if (countOfTrucks > 1) {
            for (int i = trucks.size(); i < countOfTrucks; i++) {
                trucks.add(new Truck());
            }
        }
        log.info("Создан первый грузовик.");
        return trucks;
    }

    private static void sortPackages(List<Package> packageList) {
        packageList.sort((a, b) -> {
            int heightDiff = Integer.compare(b.getType().getHeight(), a.getType().getHeight());
            if (heightDiff == 0) {
                return Integer.compare(b.getType().getWidth(), a.getType().getWidth());
            }
            return heightDiff;
        });
        log.info("Упаковки отсортированы по высоте и ширине.");
    }

    public void printTrucks(List<Truck> trucks) {
        log.info("Начало вывода состояния всех грузовиков. Всего грузовиков: {}", trucks.size());
        int truckNumber = 1;
        for (Truck truck : trucks) {
            System.out.printf("Truck %d%n", truckNumber);
            printTruck(truck);
            truckNumber++;
        }
        log.info("Вывод завершён.");
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

    public List<Truck> addPackagesToIndividualTrucks(List<Package> packages) {
        List<Truck> trucks = new ArrayList<>();
        for (Package pkg : packages) {
            Truck truck = new Truck();
            packingService.addPackage(truck, pkg);
            trucks.add(truck);
            log.info("Упаковка {} добавлена в новый грузовик.", pkg.getId());
        }
        return trucks;
    }
}