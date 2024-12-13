package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PackageLoader {

    public static List<Package> readPackages(String filePath) throws IOException {
        try (Stream<String> lines = new BufferedReader(new FileReader(filePath)).lines()) {
            StringBuilder currentPackage = new StringBuilder();
            List<Package> packages = new ArrayList<>();

            lines.forEach(line -> {
                if (!line.trim().isEmpty()) {
                    currentPackage.append(line).append("\n");
                } else {
                    if (currentPackage.length() > 0) {
                        packages.add(new Package(currentPackage.toString().trim()));
                        currentPackage.setLength(0);
                    }
                }
            });
            if (currentPackage.length() > 0) {
                packages.add(new Package(currentPackage.toString().trim()));
            }
            return packages;
        }
    }

    public static Truck packPackages(List<Package> packages) {
        TruckCapacity truckCapacity = new TruckCapacity(6, 6); // Пример: 6x6
        Truck truck = new Truck(truckCapacity);
        truck.packPackages(packages);
        return truck;
    }
}