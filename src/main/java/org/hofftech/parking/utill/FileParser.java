package org.hofftech.parking.utill;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.enums.ParcelType;
import org.hofftech.parking.model.dto.ParcelDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileParser {
    public List<ParcelDto> parsePackages(List<String> lines) {
        List<ParcelDto> parcelDtos = new ArrayList<>();
        List<String> currentShape = new ArrayList<>();
        int packageId = 1;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (!currentShape.isEmpty()) {
                    ParcelDto pkg = createPackage(currentShape, packageId++);
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
            ParcelDto pkg = createPackage(currentShape, packageId++);
            if (pkg != null) {
                parcelDtos.add(pkg);
            }
        }

        log.info("Успешно распознано {} упаковок.", parcelDtos.size());
        return parcelDtos;
    }

    private ParcelDto createPackage(List<String> shapeLines, int id) {
        try {
            ParcelType parcelType = ParcelType.fromShape(shapeLines);
            return new ParcelDto(parcelType, id, null);
        } catch (Exception e) {
            log.error("Ошибка создания упаковки с ID {}: {}", id, e.getMessage());
            return null;
        }
    }
}