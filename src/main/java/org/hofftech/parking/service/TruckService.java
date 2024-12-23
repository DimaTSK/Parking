package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.TruckDto;
import org.hofftech.parking.model.dto.ParcelDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TruckService {
    private final ParcelService parcelService;

    public TruckService(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    public List<TruckDto> addParcelsToMultipleTrucks(List<ParcelDto> parcelDtoList, int maxTrucks, Boolean evenAlg) {
        log.info("Начало размещения упаковок. Всего упаковок: {}", parcelDtoList.size());

        sortParcels(parcelDtoList);
        List<TruckDto> truckDtos;

        if (!evenAlg) {
            truckDtos = createTruck(1);
            placeParcels(parcelDtoList, truckDtos, maxTrucks);
        } else {
            truckDtos = createTruck(maxTrucks);
            distributeParcelsEvenly(parcelDtoList, truckDtos);
        }

        log.info("Размещение завершено. Всего грузовиков: {}", truckDtos.size());
        return truckDtos;
    }

    public void distributeParcelsEvenly(List<ParcelDto> parcelDtos, List<TruckDto> truckDtos) {
        if (truckDtos.isEmpty()) {
            log.error("Количество грузовиков не может быть равно 0.");
            throw new IllegalArgumentException("Невозможно распределить посылки: нет грузовиков.");
        }

        int totalParcels = parcelDtos.size();
        int numberOfTrucks = truckDtos.size();
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
            TruckDto truckDto = truckDtos.get(i);
            List<ParcelDto> group = truckParcels.get(i);

            log.info("заполняем грузовик {} из пачки {} посылок.", i + 1, group.size());
            for (ParcelDto pkg : group) {
                if (!parcelService.addParcels(truckDto, pkg)) {
                    log.error("Не получилось определить посылку {} в грузовик {}.", pkg.getType(), i + 1);
                    throw new RuntimeException("Не хватает кол-ва грузовиков для определения!");
                }
            }
        }

        log.info("Посылки распределены по грузовикам.");
    }

    private void placeParcels(List<ParcelDto> parcelDtoList, List<TruckDto> truckDtos, int maxTrucks) {
        for (ParcelDto pkg : parcelDtoList) {
            log.info("Пытаемся разместить упаковку {} с ID {}", pkg.getType(), pkg.getId());
            boolean placed = false;
            for (TruckDto truckDto : truckDtos) {
                if (parcelService.addParcels(truckDto, pkg)) {
                    log.info("Упаковка {} с ID {} успешно размещена в существующем грузовике.", pkg.getType(), pkg.getId());
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                log.info("Упаковка {} с ID {} не поместилась. Создаём новый грузовик.", pkg.getType(), pkg.getId());
                if (truckDtos.size() < maxTrucks) {
                    TruckDto newTruckDto = new TruckDto();
                    if (parcelService.addParcels(newTruckDto, pkg)) {
                        truckDtos.add(newTruckDto);
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

    private static List<TruckDto> createTruck(int countOfTrucks) {
        List<TruckDto> truckDtos = new ArrayList<>();
        TruckDto currentTruckDto = new TruckDto();
        truckDtos.add(currentTruckDto);
        if (countOfTrucks > 1) {
            for (int i = truckDtos.size(); i < countOfTrucks; i++) {
                truckDtos.add(new TruckDto());
            }
        }
        log.info("Создан первый грузовик.");
        return truckDtos;
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

    public void printTrucks(List<TruckDto> truckDtos) {
        log.info("Начало вывода состояния всех грузовиков. Всего грузовиков: {}", truckDtos.size());
        int truckNumber = 1;
        for (TruckDto truckDto : truckDtos) {
            System.out.printf("Truck %d%n", truckNumber);
            printTruck(truckDto);
            truckNumber++;
        }
        log.info("Вывод завершён.");
    }

    private void printTruck(TruckDto truckDto) {
        for (int y = truckDto.getHEIGHT() - 1; y >= 0; y--) {
            System.out.print("+");
            for (int x = 0; x < truckDto.getWIDTH(); x++) {
                System.out.print(truckDto.getGrid()[y][x]);
            }
            System.out.println("+");
        }
        System.out.println("++++++++" + "\n");
    }

    public List<TruckDto> addParcelsToIndividualTrucks(List<ParcelDto> parcelDtos) {
        List<TruckDto> truckDtos = new ArrayList<>();
        for (ParcelDto pkg : parcelDtos) {
            TruckDto truckDto = new TruckDto();
            parcelService.addParcels(truckDto, pkg);
            truckDtos.add(truckDto);
            log.info("Упаковка {} добавлена в новый грузовик.", pkg.getId());
        }
        return truckDtos;
    }
}
