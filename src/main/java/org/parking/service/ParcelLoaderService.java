package org.parking.service;

import org.parking.model.dto.TruckCapacityDto;
import org.parking.model.dto.ParcelDto;

import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ParcelLoaderService {

    public static List<ParcelDto> readPackages(String filePath) throws IOException {
        List<ParcelDto> parcelDtos = new ArrayList<>();
        StringBuilder currentPackage = new StringBuilder();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(line -> {
                if (!line.trim().isEmpty()) {
                    currentPackage.append(line).append("\n");
                } else {
                    if (currentPackage.length() > 0) {
                        parcelDtos.add(new ParcelDto(currentPackage.toString().trim()));
                        currentPackage.setLength(0);
                    }
                }
            });
            if (currentPackage.length() > 0) {
                parcelDtos.add(new ParcelDto(currentPackage.toString().trim()));
            }
        }

        return parcelDtos;
    }

    public static TruckService packPackages(List<ParcelDto> parcelDtos) {
        TruckCapacityDto truckCapacityDto = new TruckCapacityDto(6, 6); // Пример: 6x6
        TruckService truckService = new TruckService(truckCapacityDto);
        truckService.packPackages(parcelDtos);
        return truckService;
    }
}