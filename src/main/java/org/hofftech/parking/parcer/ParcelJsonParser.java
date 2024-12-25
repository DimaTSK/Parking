package org.hofftech.parking.parcer;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.enums.ParcelType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ParcelJsonParser {
    public List<String> extractParcels(Map<String, Object> jsonData) {
        List<String> parcelsOutput = new ArrayList<>();
        List<Map<String, Object>> trucks = (List<Map<String, Object>>) jsonData.get("trucks");

        if (trucks == null) {
            log.warn("Ключ 'trucks' отсутствует или имеет неверный тип.");
            return parcelsOutput;
        }

        for (Map<String, Object> truck : trucks) {
            List<Map<String, Object>> parcels = (List<Map<String, Object>>) truck.get("parcels");
            if (parcels == null) {
                log.warn("В грузовике отсутствуют посылки или имеют неверный тип.");
                continue;
            }

            for (Map<String, Object> pkg : parcels) {
                String type = (String) pkg.get("type");
                if (type == null) {
                    log.warn("У посылки отсутствует тип.");
                    continue;
                }

                try {
                    List<String> shape = ParcelType.valueOf(type).getShape();
                    parcelsOutput.addAll(shape);
                    parcelsOutput.add("");
                } catch (IllegalArgumentException e) {
                    log.error("Неизвестный тип посылки: {}", type);
                }
            }
        }

        return parcelsOutput;
    }
}