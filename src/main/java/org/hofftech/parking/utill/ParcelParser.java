package org.hofftech.parking.utill;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.ParcelPosition;
import org.hofftech.parking.model.enums.ParcelType;
import org.hofftech.parking.model.dto.ParcelDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ParcelParser {
    public List<ParcelDto> parseParcels(List<String> lines) {
        List<ParcelDto> parcelDtos = new ArrayList<>();
        List<String> currentShape = new ArrayList<>();
        int parcelId = 1;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (!currentShape.isEmpty()) {
                    ParcelDto pkg = createParcel(currentShape, parcelId++);
                    if (pkg != null) {
                        parcelDtos.add(pkg);
                    }
                    currentShape.clear();
                }
            } else {
                currentShape.add(line);
            }
        }

        if (!currentShape.isEmpty()) {
            ParcelDto pkg = createParcel(currentShape, parcelId++);
            if (pkg != null) {
                parcelDtos.add(pkg);
            }
        }

        log.info("Успешно распознано {} упаковок.", parcelDtos.size());
        return parcelDtos;
    }

    private ParcelDto createParcel(List<String> shapeLines, int id) {
        try {
            ParcelType parcelType = ParcelType.fromShape(shapeLines);
            ParcelPosition initialPosition = new ParcelPosition(-1, -1);

            return new ParcelDto(parcelType, id, initialPosition);
        } catch (Exception e) {
            log.error("Ошибка создания упаковки с ID {}: {}", id, e.getMessage());
            return null;
        }
    }
}