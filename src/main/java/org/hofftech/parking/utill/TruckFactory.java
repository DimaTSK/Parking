package org.hofftech.parking.utill;

import org.hofftech.parking.model.entity.TruckEntity;

import java.util.ArrayList;
import java.util.List;

public class TruckFactory {
    public static List<TruckEntity> createTrucks(int countOfTrucks) {
        List<TruckEntity> truckEntities = new ArrayList<>();
        for (int i = 0; i < countOfTrucks; i++) {
            truckEntities.add(new TruckEntity());
        }
        return truckEntities;
    }
}