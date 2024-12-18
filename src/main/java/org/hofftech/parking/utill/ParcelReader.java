package org.hofftech.parking.utill;

import org.hofftech.parking.model.dto.ParcelDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParcelReader {
    public List<ParcelDto> readPackages(String filePath) throws IOException {
        List<ParcelDto> parcelDtos = new ArrayList<>();
        StringBuilder currentPackage = new StringBuilder();
        ParcelValidator validator = new ParcelValidator();

        List<String> lines = Files.readAllLines(Paths.get(filePath));

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                currentPackage.append(line).append("\n");
            } else {
                if (isStringBuilderNotEmpty(currentPackage)) {
                    String[] packageLines = currentPackage.toString().trim().split("\n");
                    for (String packageLine : packageLines) {
                        validator.validate(packageLine);
                    }
                    parcelDtos.add(new ParcelDto(packageLines));
                    currentPackage.setLength(0);
                }
            }
        }
        if (isStringBuilderNotEmpty(currentPackage)) {
            String[] packageLines = currentPackage.toString().trim().split("\n");
            for (String packageLine : packageLines) {
                validator.validate(packageLine);
            }
            parcelDtos.add(new ParcelDto(packageLines));
        }

        return parcelDtos;
    }

    private boolean isStringBuilderNotEmpty(StringBuilder sb) {
        return !sb.isEmpty();
    }
}