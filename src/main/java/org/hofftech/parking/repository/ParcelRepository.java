package org.hofftech.parking.repository;

import org.hofftech.parking.exception.ParcelNameException;
import org.hofftech.parking.exception.ParcelNotFoundException;
import org.hofftech.parking.model.enums.DefaultParcelType;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий для управления данными о посылках.
 * Предоставляет методы для добавления, редактирования, удаления и поиска посылок.
 */
public class ParcelRepository {
    private static final int FIRST_CHAR = 0;
    private static final int START_POSITION_X = 0;
    private static final int START_POSITION_Y = 0;

    private final Map<String, Parcel> parcels = new HashMap<>();

    public void addParcel(Parcel providedParcel) {
        if (parcels.containsKey(providedParcel.getName())) {
            throw new ParcelNameException("Посылка с таким именем уже существует: " + providedParcel.getName());
        }
        parcels.put(providedParcel.getName(), providedParcel);
    }

    public Optional<Parcel> findParcel(String name) {
        for (String key : parcels.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return Optional.of(parcels.get(key));
            }
        }
        return Optional.empty();
    }

    public void editParcel(String name, Parcel updatedParcel) {
        if (!parcels.containsKey(name)) {
            throw new ParcelNotFoundException("Посылка не найдена: " + name);
        }
        parcels.put(name, updatedParcel);
    }

    public void deleteParcel(String name) {
        if (!parcels.containsKey(name)) {
            throw new ParcelNotFoundException("Посылка не найдена: " + name);
        }
        parcels.remove(name);
    }

    public List<Parcel> getAllParcel() {
        return parcels.values().stream()
                .sorted(Comparator.comparing(Parcel::getName))
                .toList();
    }

    public void loadDefaultParcels() {
        int counter = 1;
        for (DefaultParcelType type : DefaultParcelType.values()) {
            String name = "Посылка Тип " + counter++;
            parcels.put(name, new Parcel(
                    name,
                    type.getShape(),
                    type.getShape().getFirst().charAt(FIRST_CHAR),
                    new ParcelStartPosition(START_POSITION_X, START_POSITION_Y)
            ));
        }
    }
}