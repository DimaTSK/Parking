package org.parking.utill;

import org.parking.model.dto.ParcelDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParcelReader {
    // Метод для чтения пакетов из файла
    public List<ParcelDto> readPackages(String filePath) throws IOException {
        List<ParcelDto> parcelDtos = new ArrayList<>();
        StringBuilder currentPackage = new StringBuilder();

        List<String> lines = Files.readAllLines(Paths.get(filePath));

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                currentPackage.append(line).append("\n");
            } else {
                if (isStringBuilderNotEmpty(currentPackage)) {
                    parcelDtos.add(ParcelDto.create(currentPackage.toString().trim()));
                    currentPackage.setLength(0); // Сбрасываем StringBuilder
                }
            }
        }
        if (isStringBuilderNotEmpty(currentPackage)) {
            parcelDtos.add(ParcelDto.create(currentPackage.toString().trim()));
        }

        return parcelDtos;
    }

    private boolean isStringBuilderNotEmpty(StringBuilder sb) {
        return sb.length() != 0;
    }
}