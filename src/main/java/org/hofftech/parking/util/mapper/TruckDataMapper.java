package org.hofftech.parking.util.mapper;

import org.hofftech.parking.exception.MissingParcelPositionException;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.ParcelPosition;
import org.hofftech.parking.model.entity.Truck;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TruckDataMapper {

    public List<Map<String, Object>> mapTrucks(List<Truck> truckEntities) {
        List<Map<String, Object>> trucksData = new ArrayList<>();
        for (int i = 0; i < truckEntities.size(); i++) {
            trucksData.add(mapTruck(truckEntities.get(i), i + 1));
        }
        return trucksData;
    }

    private Map<String, Object> mapTruck(Truck truck, int truckId) {
        Map<String, Object> truckMap = new LinkedHashMap<>();
        truckMap.put("truck_id", truckId);
        truckMap.put("parcels", mapParcels(truck));
        return truckMap;
    }

    private List<Map<String, Object>> mapParcels(Truck truck) {
        List<Map<String, Object>> parcelsData = new ArrayList<>();
        for (ParcelDto pkg : truck.getParcelDtos()) {
            parcelsData.add(mapParcel(pkg));
        }
        return parcelsData;
    }

    private Map<String, Object> mapParcel(ParcelDto pkg) {
        Map<String, Object> parcelsMap = new LinkedHashMap<>();
        parcelsMap.put("id", pkg.getId());
        parcelsMap.put("type", pkg.getType().name());

        ParcelPosition position = pkg.getParcelPosition();
        if (position != null) {
            parcelsMap.put("position", Map.of("x", position.getX() + 1, "y", position.getY() + 1));
        } else {
            throw new MissingParcelPositionException("У упаковки с ID " + pkg.getId() + " отсутствует стартовая позиция");
        }

        return parcelsMap;
    }
}
