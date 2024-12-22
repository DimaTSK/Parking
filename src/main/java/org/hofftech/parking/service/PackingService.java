package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.PackageStartPosition;
import org.hofftech.parking.model.Truck;
import org.hofftech.parking.model.Package;

import java.util.List;

@Slf4j
public class PackingService {
    protected boolean canAddPackage(Truck truck, Package pkg, int startX, int startY) {
        log.debug("Проверяем возможность добавить упаковку {} в координаты X={}, Y={}", pkg.getType(), startX, startY);
        List<String> shape = pkg.getType().getShape();
        int height = shape.size();

        for (int y = 0; y < height; y++) {
            int rowWidth = shape.get(y).length();
            if (startX + rowWidth > truck.getWIDTH() || startY + y >= truck.getHEIGHT()) {
                log.debug("Упаковка {} выходит за пределы грузовика", pkg.getType());
                return false;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < shape.get(y).length(); x++) {
                if (shape.get(y).charAt(x) != ' ' && truck.getGrid()[startY + y][startX + x] != ' ') {
                    log.debug("Упаковка {} пересекается с другой упаковкой", pkg.getType());
                    return false;
                }
            }
        }

        return true;
    }

    protected boolean addPackage(Truck truck, Package pkg) {
        log.info("Пытаемся добавить упаковку {} в грузовик.", pkg.getType());

        List<String> shape = pkg.getType().getShape();
        int height = shape.size();

        for (int startY = 0; startY <= truck.getHEIGHT() - height; startY++) {
            for (int startX = 0; startX <= truck.getWIDTH() - shape.getFirst().length(); startX++) { // Слева направо
                if (canAddPackage(truck, pkg, startX, startY)) {
                    log.info("Упаковка {} успешно добавлена", pkg.getType());
                    pkg.setPackageStartPosition(new PackageStartPosition(startX, startY));
                    placePackage(truck, pkg, startX, startY);
                    return true;
                }
            }
        }


        log.warn("Упаковка {} не смогла быть добавлена в грузовик.", pkg.getType());
        return false;
    }


    protected void placePackage(Truck truck, Package pkg, int startX, int startY) {
        List<String> shape = pkg.getType().getShape();

        for (int y = 0; y < shape.size(); y++) {
            for (int x = 0; x < shape.get(y).length(); x++) {
                if (shape.get(y).charAt(x) != ' ') {
                    truck.getGrid()[startY + y][startX + x] = shape.get(y).charAt(x);
                }
            }
        }
        pkg.setPackageStartPosition(new PackageStartPosition(startX, startY));
        truck.getPackages().add(pkg);
        log.info("Упаковка {} размещена на грузовике", pkg.getType());
    }


}