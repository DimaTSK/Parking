package org.hofftech.parking.util;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.ParcelCreationException;
import org.hofftech.parking.model.dto.ParcelPosition;
import org.hofftech.parking.model.enums.ParcelType;
import org.hofftech.parking.model.dto.ParcelDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ParcelParser {

    private static final int UNDEFINED_POSITION_X = -1;
    private static final int UNDEFINED_POSITION_Y = -1;

    public List<ParcelDto> parseParcels(List<String> lines) throws ParcelCreationException {
        List<ParcelDto> parcelDtos = new ArrayList<>();
        List<String> currentShape = new ArrayList<>();
        int parcelId = 1;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (!currentShape.isEmpty()) {
                    ParcelDto pkg = createParcel(currentShape, parcelId++);
                    parcelDtos.add(pkg);
                    currentShape.clear();
                }
            } else {
                currentShape.add(line);
            }
        }

        if (!currentShape.isEmpty()) {
            ParcelDto pkg = createParcel(currentShape, parcelId);
            parcelDtos.add(pkg);
        }

        log.info("Успешно распознано {} упаковок.", parcelDtos.size());
        return parcelDtos;
    }

    private ParcelDto createParcel(List<String> shapeLines, int id) throws ParcelCreationException {
        try {
            ParcelType parcelType = ParcelType.fromShape(shapeLines);

            ParcelPosition initialPosition = new ParcelPosition(UNDEFINED_POSITION_X, UNDEFINED_POSITION_Y);

            return new ParcelDto(parcelType, id, initialPosition);
        } catch (Exception e) {
            String errorMessage = String.format("Ошибка создания упаковки с ID %d: %s", id, e.getMessage());
            log.error(errorMessage);
            throw new ParcelCreationException(errorMessage, e);
        }
    }
}