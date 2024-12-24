package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.entity.TruckEntity;
import org.hofftech.parking.model.dto.ParcelDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TruckService {
    private final ParcelService parcelService;

    public TruckService(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    public List<TruckEntity> addParcelsToMultipleTrucks(List<ParcelDto> parcelDtoList, int maxTrucks, Boolean evenAlg) {
        log.info("Начало размещения упаковок. Всего упаковок: {}", parcelDtoList.size());

        sortParcels(parcelDtoList);
        List<TruckEntity> truckEntities;

        if (!evenAlg) {
            truckEntities = createTruck(1);
            placeParcels(parcelDtoList, truckEntities, maxTrucks);
        } else {
            truckEntities = createTruck(maxTrucks);
            distributeParcelsEvenly(parcelDtoList, truckEntities);
        }

        log.info("Посылки размещены, количество грузовиков: {}", truckEntities.size());
        return truckEntities;
    }

    public void distributeParcelsEvenly(List<ParcelDto> parcelDtos, List<TruckEntity> truckEntities) {
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
            TruckEntity truckEntity = truckEntities.get(i);
            List<ParcelDto> group = truckParcels.get(i);

            log.info("Заполняем грузовик {} из пачки {} посылок.", i + 1, group.size());
            for (ParcelDto pkg : group) {
                if (!parcelService.addParcels(truckEntity, pkg)) {
                    log.error("Не получилось определить посылку {} в грузовик {}.", pkg.getType(), i + 1);
                    throw new RuntimeException("Не хватает кол-ва грузовиков для определения!");
                }
            }
        }

        log.info("Посылки распределены по грузовикам.");
    }

    private void placeParcels(List<ParcelDto> parcelDtoList, List<TruckEntity> truckEntities, int maxTrucks) {
        for (ParcelDto pkg : parcelDtoList) {
            log.info("Пытаемся разместить упаковку {} с ID {}", pkg.getType(), pkg.getId());
            boolean placed = false;
            for (TruckEntity truckEntity : truckEntities) {
                if (parcelService.addParcels(truckEntity, pkg)) {
                    log.info("Упаковка {} с ID {} успешно размещена в существующем грузовике.", pkg.getType(), pkg.getId());
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                log.info("Упаковка {} с ID {} не поместилась. Создаём новый грузовик.", pkg.getType(), pkg.getId());
                if (truckEntities.size() < maxTrucks) {
                    TruckEntity newTruckEntity = new TruckEntity();
                    if (parcelService.addParcels(newTruckEntity, pkg)) {
                        truckEntities.add(newTruckEntity);
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

    private static List<TruckEntity> createTruck(int countOfTrucks) {
        List<TruckEntity> truckEntities = new ArrayList<>();
        TruckEntity currentTruckEntity = new TruckEntity();
        truckEntities.add(currentTruckEntity);
        if (countOfTrucks > 1) {
            for (int i = truckEntities.size(); i < countOfTrucks; i++) {
                truckEntities.add(new TruckEntity());
            }
        }
        log.info("Создан первый грузовик.");
        return truckEntities;
    }

    private static void sortParcels(List<ParcelDto> parcelDtoList) {
        parcelDtoList.sort((a, b) -> {
            int heightDiff = Integer.compare(b.getType().getHeight(), a.getType().getHeight());
            if (heightDiff == 0) {
                return Integer.compare(b.getType().getWidth(), a.getType().getWidth());
            }
            return heightDiff;
        });
        log.info("Упаковки отсортированы по высоте и ширине.");
    }

    public void printTrucks(List<TruckEntity> truckEntities) {
        log.info("Всего грузовиков: {}", truckEntities.size());
        int truckNumber = 1;
        for (TruckEntity truckEntity : truckEntities) {
            System.out.printf("Truck %d%n", truckNumber);
            printTruck(truckEntity);
            truckNumber++;
        }
        log.info("Вывод завершён-------------");
    }

    private void printTruck(TruckEntity truckEntity) {
        for (int y = truckEntity.getHEIGHT() - 1; y >= 0; y--) {
            System.out.print("+");
            for (int x = 0; x < truckEntity.getWIDTH(); x++) {
                System.out.print(truckEntity.getGrid()[y][x]);
            }
            System.out.println("+");
        }
        System.out.println("++++++++" + "\n");
    }

    public List<TruckEntity> addParcelsToIndividualTrucks(List<ParcelDto> parcelDtos) {
        List<TruckEntity> truckEntities = new ArrayList<>();
        for (ParcelDto pkg : parcelDtos) {
            TruckEntity truckEntity = new TruckEntity();
            parcelService.addParcels(truckEntity, pkg);
            truckEntities.add(truckEntity);
            log.info("Упаковка {} добавлена в новый грузовик.", pkg.getId());
        }
        return truckEntities;
    }
}

