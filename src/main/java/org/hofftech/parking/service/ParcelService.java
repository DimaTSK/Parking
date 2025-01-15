package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;
import org.hofftech.parking.model.Truck;

import java.util.List;

@Slf4j
public class ParcelService {

    protected boolean canAddPackage(Truck truck, Parcel parcel, int startX, int startY) {
        log.debug("Проверяем возможность добавить упаковку {} в координаты X={}, Y={}", parcel.getName(), startX, startY);
        List<String> shape = parcel.getUniqueShape();
        int height = shape.size();

        if (checkTruckLimits(truck, parcel, startX, startY, height, shape)) return false;

        if (checkForIntersection(truck, parcel, startX, startY, height, shape)) return false;

        String topRow = shape.getFirst();
        int requiredSupport = (int) Math.ceil(topRow.length() / 2.0);
        int support = 0;
        if (startY == 0) {
            log.debug("Упаковка {} внизу грузовика, опора не требуется.", parcel.getName());
            return true;
        }

        return !checkForSupport(truck, parcel, startX, startY, topRow, support, requiredSupport);
    }

    private boolean checkForSupport(Truck truck, Parcel parcel, int startX, int startY, String topRow, int support, int requiredSupport) {
        for (int x = 0; x < topRow.length(); x++) {
            if (topRow.charAt(x) != ' ' && truck.getGrid()[startY - 1][startX + x] != ' ') {
                support++;
            }
        }
        if (support < requiredSupport) {
            log.debug("Упаковка {} не имеет достаточной опоры. Требуется {}, доступно {}", parcel.getName(), requiredSupport, support);
            return true;
        }
        return false;
    }

    private boolean checkForIntersection(Truck truck, Parcel parcel, int startX, int startY, int height, List<String> shape) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < shape.get(y).length(); x++) {
                if (shape.get(y).charAt(x) != ' ' && truck.getGrid()[startY + y][startX + x] != ' ') {
                    log.debug("Упаковка {} пересекается с другой посылкой", parcel.getName());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkTruckLimits(Truck truck, Parcel parcel, int startX, int startY, int height, List<String> shape) {
        for (int y = 0; y < height; y++) {
            int rowWidth = shape.get(y).length();
            if (startX + rowWidth > truck.getWidth() || startY + y >= truck.getHeight()) {
                log.debug("Упаковка {} выходит за пределы грузовика", parcel.getName());
                return true;
            }
        }
        return false;
    }

    protected boolean addPackage(Truck truck, Parcel parcel) {
        log.info("Пытаемся добавить упаковку {} в грузовик.", parcel.getName());

        List<String> shape = parcel.getUniqueShape();
        int height = shape.size();

        for (int startY = 0; startY <= truck.getHeight() - height; startY++) {
            for (int startX = 0; startX <= truck.getWidth() - shape.getFirst().length(); startX++) {
                if (canAddPackage(truck, parcel, startX, startY)) {
                    log.info("Упаковка {} успешно добавлена", parcel.getName());
                    parcel.setParcelStartPosition(new ParcelStartPosition(startX, startY));
                    placePackage(truck, parcel, startX, startY);
                    return true;
                }
            }
        }

        log.warn("Упаковка {} не смогла быть добавлена в грузовик.", parcel.getName());
        return false;
    }


    protected void placePackage(Truck truck, Parcel parcel, int startX, int startY) {
        List<String> shape = parcel.getUniqueShape();

        for (int y = 0; y < shape.size(); y++) {
            for (int x = 0; x < shape.get(y).length(); x++) {
                if (shape.get(y).charAt(x) != ' ') {
                    truck.getGrid()[startY + y][startX + x] = shape.get(y).charAt(x);
                }
            }
        }
        parcel.setParcelStartPosition(new ParcelStartPosition(startX, startY));
        truck.getParcels().add(parcel);
        log.info("Упаковка {} размещена на грузовике", parcel.getName());
    }


}