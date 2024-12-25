package org.hofftech.parking.utill;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.entity.Truck;

import java.util.List;

@Slf4j
public class TruckPrinter {
    public void printTrucks(List<Truck> truckEntities) {
        log.info("Всего грузовиков: {}", truckEntities.size());
        int truckNumber = 1;
        for (Truck truck : truckEntities) {
            System.out.printf("Truck %d%n", truckNumber);
            printTruck(truck);
            truckNumber++;
        }
        log.info("Вывод завершён-------------");
    }

    private void printTruck(Truck truck) {
        for (int y = truck.getHEIGHT() - 1; y >= 0; y--) {
            System.out.print("+");
            for (int x = 0; x < truck.getWIDTH(); x++) {
                System.out.print(truck.getGrid()[y][x]);
            }
            System.out.println("+");
        }
        System.out.println("++++++++" + "\n");
    }
}