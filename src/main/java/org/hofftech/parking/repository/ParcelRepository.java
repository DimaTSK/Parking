package org.hofftech.parking.repository;

import org.hofftech.parking.model.enums.DefaultPackagesType;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ParcelRepository {
    private final Map<String, Parcel> packages = new HashMap<>();

    public void addPackage(Parcel pkg) {
        if (packages.containsKey(pkg.getName())) {
            throw new IllegalArgumentException("Посылка с таким именем уже существует: " + pkg.getName());
        }
        packages.put(pkg.getName(), pkg);
    }

    public Parcel findPackage(String name) {
        for (String key : packages.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return packages.get(key);
            }
        }
        throw new IllegalArgumentException("Посылка не найдена: " + name);
    }

    public void editPackage(String name, Parcel updatedParcel) {
        if (!packages.containsKey(name)) {
            throw new IllegalArgumentException("Посылка не найдена: " + name);
        }
        packages.put(name, updatedParcel);
    }

    public void deletePackage(String name) {
        if (!packages.containsKey(name)) {
            throw new IllegalArgumentException("Посылка не найдена: " + name);
        }
        packages.remove(name);
    }

    public List<Parcel> getAllPackages() {
        return packages.values().stream()
                .sorted(Comparator.comparing(Parcel::getName))
                .collect(Collectors.toList());
    }

    public void loadDefaultPackages() {
        int counter = 1;
        for (DefaultPackagesType type : DefaultPackagesType.values()) {
            String name = "Посылка Тип " + counter++;
            packages.put(name, new Parcel(
                    name,
                    type.getShape(),
                    type.getShape().getFirst().charAt(0),
                    new ParcelStartPosition(0, 0)
            ));
        }
    }
}