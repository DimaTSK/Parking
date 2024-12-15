package org.parking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ParcelLoader {

    public static List<Parcel> readPackages(String filePath) throws IOException {
        try (Stream<String> lines = new BufferedReader(new FileReader(filePath)).lines()) {
            StringBuilder currentPackage = new StringBuilder();
            List<Parcel> parcels = new ArrayList<>();

            lines.forEach(line -> {
                if (!line.trim().isEmpty()) {
                    currentPackage.append(line).append("\n");
                } else {
                    if (currentPackage.length() > 0) {
                        parcels.add(new Parcel(currentPackage.toString().trim()));
                        currentPackage.setLength(0);
                    }
                }
            });
            if (currentPackage.length() > 0) {
                parcels.add(new Parcel(currentPackage.toString().trim()));
            }
            return parcels;
        }
    }

    public static Truck packPackages(List<Parcel> parcels) {
        TruckCapacity truckCapacity = new TruckCapacity(6, 6); // Пример: 6x6
        Truck truck = new Truck(truckCapacity);
        truck.packPackages(parcels);
        return truck;
    }
}