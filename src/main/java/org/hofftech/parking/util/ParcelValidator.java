package org.hofftech.parking.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hofftech.parking.exception.InvalidJsonStructureException;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.enums.ParcelType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ParcelValidator {

    public boolean isValidParcels(List<ParcelDto> parcelDtos) {
        List<ParcelDto> invalidParcelDtos = new ArrayList<>();
        for (ParcelDto pkg : parcelDtos) {
            if (!isValidParcel(pkg)) {
                invalidParcelDtos.add(pkg);
            }
        }
        if (!invalidParcelDtos.isEmpty()) {
            for (ParcelDto invalidPkg : invalidParcelDtos) {
                log.error("Упаковка с ID {} имеет некорректную форму: {}",
                        invalidPkg.getId(), invalidPkg.getType().getShape());
            }
            return false;
        }

        log.info("Все упаковки успешно.");
        return true;
    }

    private boolean isValidParcel(ParcelDto pkg) {
        ParcelType type = pkg.getType();
        List<String> shape = type.getShape();

        int maxWidth = 0;
        for (String row : shape) {
            maxWidth = Math.max(maxWidth, row.length());
        }

        for (String row : shape) {
            if (row.length() < maxWidth) {
                for (int x = row.length(); x < maxWidth; x++) {
                    if (row.length() > x) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void validateJsonStructure(Map<String, Object> jsonData) {
        if (!jsonData.containsKey("trucks")) {
            log.error("Ошибка: JSON не содержит ключ 'trucks'.");
            throw new InvalidJsonStructureException("Структура Json некорректа: отсутствует ключ 'trucks'.");
        }

        List<Map<String, Object>> trucks = (List<Map<String, Object>>) jsonData.get("trucks");
        for (Map<String, Object> truck : trucks) {
            if (!truck.containsKey("parcels")) continue;
            List<Map<String, Object>> parcels = (List<Map<String, Object>>) truck.get("parcels");
            for (Map<String, Object> pkg : parcels) {
                if (!pkg.containsKey("type")) {
                    log.error("У одной из посылок отсутствует ключ 'type'");
                    throw new InvalidJsonStructureException("Структура Json некорректа: у одной из посылок отсутствует ключ 'type'.");
                }
            }
        }
        log.info("JSON успешно проверен.");
    }
}
