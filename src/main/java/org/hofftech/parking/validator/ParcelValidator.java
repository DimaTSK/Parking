package org.hofftech.parking.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hofftech.parking.exception.InvalidJsonStructureException;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.enums.ParcelType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ParcelValidator {

    public boolean isValidFile(List<String> lines) {
        if (CollectionUtils.isEmpty(lines)) {
            log.error("Файл пустой.");
            return false;
        }
        log.info("Файл проверен, количество строк: {}", lines.size());
        return true;
    }

    public boolean isValidParcels(List<ParcelDto> parcelDtos) {
        List<ParcelDto> invalidParcelDtos = parcelDtos.stream()
                .filter(pkg -> !isValidParcel(pkg))
                .collect(Collectors.toList());

        if (!invalidParcelDtos.isEmpty()) {
            invalidParcelDtos.forEach(invalidPkg -> log.error(
                    "Посылка с ID {} имеет некорректную форму: {}",
                    invalidPkg.getId(), invalidPkg.getType().getShape()));
            return false;
        }

        log.info("Все посылки успешно валидированы.");
        return true;
    }

    private boolean isValidParcel(ParcelDto pkg) {
        ParcelType type = pkg.getType();
        List<String> shape = type.getShape();

        if (shape.isEmpty()) {
            log.error("Посылка с ID {} имеет пустую форму.", pkg.getId());
            return false;
        }

        int expectedLength = shape.get(0).length();
        for (String row : shape) {
            if (row.length() != expectedLength) {
                log.error("Посылка с ID {} имеет строки различной длины.", pkg.getId());
                return false;
            }
        }
        return true;
    }

    public void validateJsonStructure(Map<String, Object> jsonData) {
        if (!jsonData.containsKey("trucks")) {
            log.error("Ошибка: JSON не содержит ключ 'trucks'.");
            throw new InvalidJsonStructureException("Структура JSON некорректна: отсутствует ключ 'trucks'.");
        }

        List<Map<String, Object>> trucks = (List<Map<String, Object>>) jsonData.get("trucks");
        for (Map<String, Object> truck : trucks) {
            if (!truck.containsKey("parcels")) continue;
            List<Map<String, Object>> parcels = (List<Map<String, Object>>) truck.get("parcels");
            for (Map<String, Object> pkg : parcels) {
                if (!pkg.containsKey("type")) {
                    log.error("У одной из посылок отсутствует ключ 'type'.");
                    throw new InvalidJsonStructureException("Структура JSON некорректна: у одной из посылок отсутствует ключ 'type'.");
                }
            }
        }
        log.info("JSON успешно проверен.");
    }
}
