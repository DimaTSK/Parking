package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.ParcelPosition;
import org.hofftech.parking.model.entity.Truck;
import org.hofftech.parking.model.dto.ParcelDto;

import java.util.List;

@Slf4j
public class ParcelService {

    public boolean canAddParcel(Truck truck, ParcelDto pkg, int startX, int startY) {
        log.debug("Проверка возможности добавить упаковку {} в координаты X={}, Y={}", pkg.getType(), startX, startY);
        List<String> shape = pkg.getType().getShape();
        int height = shape.size();

        return isWithinTruckBounds(truck, shape, startX, startY, height) &&
                !isOverlappingWithExistingParcels(truck, shape, startX, startY);
    }

    public boolean isWithinTruckBounds(Truck truck, List<String> shape, int startX, int startY, int height) {
        for (int y = 0; y < height; y++) {
            int rowWidth = shape.get(y).length();
            if (startX + rowWidth > Truck.getWIDTH() || startY + y >= Truck.getHEIGHT()) {
                log.debug("Упаковка {} выходит за пределы грузовика", shape);
                return false;
            }
        }
        return true;
    }


    public boolean isOverlappingWithExistingParcels(Truck truck, List<String> shape, int startX, int startY) {
        for (int y = 0; y < shape.size(); y++) {
            for (int x = 0; x < shape.get(y).length(); x++) {
                if (shape.get(y).charAt(x) != ' ' && truck.getGrid()[startY + y][startX + x] != ' ') {
                    log.debug("Упаковка {} пересекается с другой упаковкой", shape);
                    return true;
                }
            }
        }
        return false;
    }


    public boolean addParcels(Truck truck, ParcelDto pkg) {
        log.info("Пытаемся добавить упаковку {} в грузовик.", pkg.getType());
        List<String> shape = pkg.getType().getShape();
        int height = shape.size();

        for (int startY = 0; startY <= Truck.getHEIGHT() - height; startY++) {
            for (int startX = 0; startX <= Truck.getWIDTH() - shape.get(0).length(); startX++) {
                if (canAddParcel(truck, pkg, startX, startY)) {
                    placeParcel(truck, pkg, startX, startY);
                    return true;
                }
            }
        }

        log.warn("Упаковка {} не смогла быть добавлена в грузовик.", pkg.getType());
        return false;
    }


    private void placeParcel(Truck truck, ParcelDto pkg, int startX, int startY) {
        List<String> shape = pkg.getType().getShape();

        for (int y = 0; y < shape.size(); y++) {
            for (int x = 0; x < shape.get(y).length(); x++) {
                if (shape.get(y).charAt(x) != ' ') {
                    truck.getGrid()[startY + y][startX + x] = shape.get(y).charAt(x);
                }
            }
        }
        pkg.setParcelPosition(new ParcelPosition(startX, startY));
        truck.getParcelDtos().add(pkg);
        log.info("Упаковка {} размещена на грузовике", pkg.getType());
    }

    public void placeParcels(List<ParcelDto> parcelDtoList, List<Truck> truckEntities, int maxTrucks) {
        for (ParcelDto pkg : parcelDtoList) {
            boolean placed = false;

            for (Truck truck : truckEntities) {
                if (addParcels(truck, pkg)) {
                    placed = true;
                    log.info("Упаковка {} размещена в существующем грузовике.", pkg.getType());
                    break;
                }
            }

            if (!placed) {
                if (truckEntities.size() < maxTrucks) {
                    Truck newTruck = new Truck();
                    log.info("Создаётся новый грузовик для размещения упаковки {}.", pkg.getType());
                    if (addParcels(newTruck, pkg)) {
                        truckEntities.add(newTruck);
                        placed = true;
                        log.info("Упаковка {} размещена в новом грузовике.", pkg.getType());
                    } else {
                        log.error("Упаковка {} с ID {} не может быть размещена даже в новом грузовике.",
                                pkg.getType(), pkg.getId());
                        throw new RuntimeException("Упаковка " + pkg.getType() + " с ID " + pkg.getId() +
                                " не может быть размещена даже в новом грузовике.");
                    }
                } else {
                    log.error("Превышен лимит грузовиков: {}", maxTrucks);
                    throw new RuntimeException("Превышен лимит грузовиков: " + maxTrucks);
                }
            }
        }
        log.info("Все посылки успешно размещены по грузовикам.");
    }
}