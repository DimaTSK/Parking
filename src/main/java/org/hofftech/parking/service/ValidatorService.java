package org.hofftech.parking.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hofftech.parking.model.Package;
import org.hofftech.parking.model.PackageType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ValidatorService {
    public boolean isValidFile(List<String> lines) {
        if (CollectionUtils.isEmpty(lines)) {
            log.error("Файл пустой или не содержит данных.");
            return false;
        }
        log.info("Файл успешно проверен. Количество строк: {}", lines.size());
        return true;
    }

    public boolean isValidPackages(List<Package> packages) {
        List<Package> invalidPackages = new ArrayList<>();
        for (Package pkg : packages) {
            if (!isValidPackage(pkg)) {
                invalidPackages.add(pkg);
            }
        }
        if (!invalidPackages.isEmpty()) {
            for (Package invalidPkg : invalidPackages) {
                log.error("Упаковка с ID {} имеет некорректную форму: {}",
                        invalidPkg.getId(), invalidPkg.getType().getShape());
            }
            return false;
        }

        log.info("Все упаковки успешно проверены.");
        return true;
    }

    private boolean isValidPackage(Package pkg) {
        PackageType type = pkg.getType();
        List<String> shape = type.getShape();

        // Проверяем, что форма упаковки не имеет "выпадающих" символов
        int maxWidth = 0;
        for (String row : shape) {
            maxWidth = Math.max(maxWidth, row.length());
        }

        for (String row : shape) {
            // Если в строке справа от длины строки есть символы, это ошибка
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

    public boolean isValidJsonStructure(Map<String, Object> jsonData) {
        if (!jsonData.containsKey("trucks")) {
            log.error("Ошибка: JSON не содержит ключ 'trucks'.");
            return false;
        }

        List<Map<String, Object>> trucks = (List<Map<String, Object>>) jsonData.get("trucks");
        for (Map<String, Object> truck : trucks) {
            if (!truck.containsKey("packages")) continue;
            List<Map<String, Object>> packages = (List<Map<String, Object>>) truck.get("packages");
            for (Map<String, Object> pkg : packages) {
                if (!pkg.containsKey("type")) {
                    log.error("У одной из посылок отсутствует ключ 'type'");
                    return false;
                }
            }
        }
        log.info("JSON успешно провалидирован.");
        return true;
    }

    public boolean isFileExists(File jsonFile) {
        return jsonFile.exists();
    }


}