package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;
import org.hofftech.parking.model.Truck;

import java.util.List;

@Slf4j
public class ParcelService {

    private static final int START_Y_POSITION = 0;
    private static final int START_X_POSITION = 0;
    private static final char EMPTY_SPACE = ' ';
    private int ROWLENGTH_FIRT_SYMBOL;

    protected boolean canAddParcel(Truck truck, Parcel providedParcel, int startX, int startY) {
        log.debug("Проверяем возможность добавить упаковку {} в координаты X={}, Y={}", providedParcel.getName(), startX, startY);
        List<String> shape = providedParcel.getReversedShape();
        int height = shape.size();

        if (isParcelWithinLimits(truck, providedParcel, startX, startY, height, shape)) return false;

        if (isIntersection(truck, providedParcel, startX, startY, height, shape)) return false;

        String topRow = shape.getFirst();
        double requiredSupport = Math.ceil(topRow.length() / 2.0);
        int support = 0;
        if (startY == 0) {
            log.debug("Упаковка {} внизу грузовика, опора не требуется.", providedParcel.getName());
            return true;
        }

        return !isParcelSupported(truck, providedParcel, startX, startY, topRow, support, requiredSupport);
    }

    private boolean isParcelSupported(Truck truck, Parcel parcel, int startX, int startY, String topRow, int support, double requiredSupport) {
        ROWLENGTH_FIRT_SYMBOL = 0;
        for (int x = ROWLENGTH_FIRT_SYMBOL; x < topRow.length(); x++) {
            if (topRow.charAt(x) != EMPTY_SPACE && truck.getGrid()[startY - 1][startX + x] != EMPTY_SPACE) {
                support++;
            }
        }
        if (support < requiredSupport) {
            log.debug("Упаковка {} не имеет достаточной опоры. Требуется {}, доступно {}", parcel.getName(), requiredSupport, support);
            return true;
        }
        return false;
    }

    private boolean isIntersection(Truck truck, Parcel parcel, int startX, int startY, int height, List<String> shape) {
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

    private boolean isParcelWithinLimits(Truck truck, Parcel parcel, int startX, int startY, int height, List<String> shape) {
        for (int y = 0; y < height; y++) {
            int rowWidth = shape.get(y).length();
            if (startX + rowWidth > truck.getWidth() || startY + y >= truck.getHeight()) {
                log.debug("Упаковка {} выходит за пределы грузовика", parcel.getName());
                return true;
            }
        }
        return false;
    }

    protected boolean tryPack(Truck truck, Parcel providedParcel) {
        log.info("Пытаемся добавить упаковку {} в грузовик.", providedParcel.getName());

        List<String> shape = providedParcel.getReversedShape();
        int height = shape.size();

        for (int startY = START_Y_POSITION; startY <= truck.getHeight() - height; startY++) {
            for (int startX = START_X_POSITION; startX <= truck.getWidth() - shape.getFirst().length(); startX++) {
                if (canAddParcel(truck, providedParcel, startX, startY)) {
                    log.info("Упаковка {} успешно добавлена", providedParcel.getName());
                    providedParcel.setParcelStartPosition(new ParcelStartPosition(startX, startY));
                    placeParcel(truck, providedParcel, startX, startY);
                    return true;
                }
            }
        }

        log.warn("Упаковка {} не смогла быть добавлена в грузовик.", providedParcel.getName());
        return false;
    }

    protected void placeParcel(Truck truck, Parcel parcel, int startX, int startY) {
        List<String> shape = parcel.getReversedShape();

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