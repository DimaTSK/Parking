package org.hofftech.parking.utill;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.ParcelCreationException;
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
                    try {
                        ParcelDto pkg = createParcel(currentShape, parcelId++);
                        parcelDtos.add(pkg);
                    } catch (ParcelCreationException e) {
                        log.error("Не удалось создать упаковку: {}", e.getMessage());
                    }
                    currentShape.clear();
                }
            } else {
                currentShape.add(line);
            }
        }

        if (!currentShape.isEmpty()) {
            try {
                ParcelDto pkg = createParcel(currentShape, parcelId);
                parcelDtos.add(pkg);
            } catch (ParcelCreationException e) {
                log.error("Не удалось создать упаковку: {}", e.getMessage());
            }
        }

        log.info("Успешно распознано {} упаковок.", parcelDtos.size());
        return parcelDtos;}

    private ParcelDto createParcel(List<String> shapeLines, int id) throws ParcelCreationException {
        try {
            ParcelType parcelType = ParcelType.fromShape(shapeLines);

            // Создаем ParcelPosition с начальными значениями, указывающими, что позиция не установлена
            ParcelPosition initialPosition = new ParcelPosition(-1, -1);

            return new ParcelDto(parcelType, id, initialPosition);
        } catch (Exception e) {
            String errorMessage = String.format("Ошибка создания упаковки с ID %d: %s", id, e.getMessage());
            log.error(errorMessage);
            throw new ParcelCreationException(errorMessage, e);
        }
    }
}