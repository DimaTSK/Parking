package org.hofftech.parking.utill;

import org.hofftech.parking.model.entity.Truck;

import java.util.ArrayList;
import java.util.List;

public class TruckFactory {
    public List<Truck> createTrucks(int countOfTrucks) {
        List<Truck> truckEntities = new ArrayList<>();
        for (int i = 0; i < countOfTrucks; i++) {
            truckEntities.add(new Truck());
        }
        return truckEntities;
    }
}