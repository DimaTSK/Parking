package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.ParcelPosition;
import org.hofftech.parking.model.entity.Truck;
import org.hofftech.parking.model.dto.ParcelDto;

import java.util.List;

@Slf4j
public class ParcelService {

    protected boolean canAddParcel(Truck truck, ParcelDto pkg, int startX, int startY) {
        log.debug("Проверка возможности добавить упаковку {} в координаты X={}, Y={}", pkg.getType(), startX, startY);
        List<String> shape = pkg.getType().getShape();
        int height = shape.size();

        return isWithinTruckBounds(truck, shape, startX, startY, height) &&
                !isOverlappingWithExistingParcels(truck, shape, startX, startY);
    }

    private boolean isWithinTruckBounds(Truck truck, List<String> shape, int startX, int startY, int height) {
        for (int y = 0; y < height; y++) {
            int rowWidth = shape.get(y).length();
            if (startX + rowWidth > truck.getWIDTH() || startY + y >= truck.getHEIGHT()) {
                log.debug("Упаковка {} выходит за пределы грузовика", shape);
                return false;
            }
        }
        return true;
    }

    private boolean isOverlappingWithExistingParcels(Truck truck, List<String> shape, int startX, int startY) {
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

    protected boolean addParcels(Truck truck, ParcelDto pkg) {
        log.info("Пытаемся добавить упаковку {} в грузовик.", pkg.getType());
        List<String> shape = pkg.getType().getShape();
        int height = shape.size();

        for (int startY = 0; startY <= truck.getHEIGHT() - height; startY++) {
            for (int startX = 0; startX <= truck.getWIDTH() - shape.get(0).length(); startX++) {
                if (canAddParcel(truck, pkg, startX, startY)) {
                    placeParcels(truck, pkg, startX, startY);
                    return true;
                }
            }
        }

        log.warn("Упаковка {} не смогла быть добавлена в грузовик.", pkg.getType());
        return false;
    }

    protected void placeParcels(Truck truck, ParcelDto pkg, int startX, int startY) {
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
}