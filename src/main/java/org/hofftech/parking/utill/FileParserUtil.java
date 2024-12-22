package org.hofftech.parking.utill;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.PackageType;
import org.hofftech.parking.model.Package;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileParserUtil {
    public List<Package> parsePackages(List<String> lines) {
        List<Package> packages = new ArrayList<>();
        List<String> currentShape = new ArrayList<>();
        int packageId = 1;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (!currentShape.isEmpty()) {
                    Package pkg = createPackage(currentShape, packageId++);
                    if (pkg != null) {
                        packages.add(pkg);
                    }
                    currentShape.clear();
                }
            } else {
                currentShape.add(line);
            }
        }

        // Обработка последней упаковки
        if (!currentShape.isEmpty()) {
            Package pkg = createPackage(currentShape, packageId++);
            if (pkg != null) {
                packages.add(pkg);
            }
        }

        log.info("Успешно распознано {} упаковок.", packages.size());
        return packages;
    }

    private Package createPackage(List<String> shapeLines, int id) {
        try {
            PackageType packageType = PackageType.fromShape(shapeLines);
            return new Package(packageType, id, null);
        } catch (Exception e) {
            log.error("Ошибка при создании упаковки с ID {}: {}", id, e.getMessage());
            return null;
        }
    }
}