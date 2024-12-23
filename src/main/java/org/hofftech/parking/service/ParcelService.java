package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.ParcelPositionDto;
import org.hofftech.parking.model.dto.TruckDto;
import org.hofftech.parking.model.dto.ParcelDto;

import java.util.List;

@Slf4j
public class ParcelService {

    protected boolean canAddPackage(TruckDto truckDto, ParcelDto pkg, int startX, int startY) {
        log.debug("Проверка возможности добавить упаковку {} в координаты X={}, Y={}", pkg.getType(), startX, startY);
        List<String> shape = pkg.getType().getShape();
        int height = shape.size();

        if (!isWithinTruckBounds(truckDto, shape, startX, startY, height)) {
            return false;
        }

        if (isOverlappingWithExistingPackages(truckDto, shape, startX, startY)) {
            return false;
        }

        return true;
    }

    private boolean isWithinTruckBounds(TruckDto truckDto, List<String> shape, int startX, int startY, int height) {
        for (int y = 0; y < height; y++) {
            int rowWidth = shape.get(y).length();
            if (startX + rowWidth > truckDto.getWIDTH() || startY + y >= truckDto.getHEIGHT()) {
                log.debug("Упаковка {} выходит за пределы грузовика", shape);
                return false;
            }
        }
        return true;
    }

    private boolean isOverlappingWithExistingPackages(TruckDto truckDto, List<String> shape, int startX, int startY) {
        for (int y = 0; y < shape.size(); y++) {
            for (int x = 0; x < shape.get(y).length(); x++) {
                if (shape.get(y).charAt(x) != ' ' && truckDto.getGrid()[startY + y][startX + x] != ' ') {
                    log.debug("Упаковка {} пересекается с другой упаковкой", shape);
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean addPackage(TruckDto truckDto, ParcelDto pkg) {
        log.info("Пытаемся добавить упаковку {} в грузовик.", pkg.getType());
        List<String> shape = pkg.getType().getShape();
        int height = shape.size();

        for (int startY = 0; startY <= truckDto.getHEIGHT() - height; startY++) {
            for (int startX = 0; startX <= truckDto.getWIDTH() - shape.get(0).length(); startX++) {
                if (canAddPackage(truckDto, pkg, startX, startY)) {
                    placePackage(truckDto, pkg, startX, startY);
                    return true;
                }
            }
        }

        log.warn("Упаковка {} не смогла быть добавлена в грузовик.", pkg.getType());
        return false;
    }

    protected void placePackage(TruckDto truckDto, ParcelDto pkg, int startX, int startY) {
        List<String> shape = pkg.getType().getShape();

        for (int y = 0; y < shape.size(); y++) {
            for (int x = 0; x < shape.get(y).length(); x++) {
                if (shape.get(y).charAt(x) != ' ') {
                    truckDto.getGrid()[startY + y][startX + x] = shape.get(y).charAt(x);
                }
            }
        }
        pkg.setParcelPositionDto(new ParcelPositionDto(startX, startY));
        truckDto.getParcelDtos().add(pkg);
        log.info("Упаковка {} размещена на грузовике", pkg.getType());
    }
}