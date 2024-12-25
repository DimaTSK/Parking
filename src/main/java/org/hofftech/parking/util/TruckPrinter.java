package org.hofftech.parking.util;

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
        char[][] grid = truck.getGrid();
        if (grid == null || grid.length == 0) {
            log.warn("Сетка грузовика пуста.");
            return;
        }

        int height = grid.length;
        int width = grid[0].length;

        for (int y = height - 1; y >= 0; y--) {
            System.out.print("+");
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println("+");
        }
        System.out.println("+" + "-".repeat(width) + "+" + "\n");
    }
}
