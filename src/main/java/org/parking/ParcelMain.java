package org.parking;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static org.parking.ParcelLoader.packPackages;
import static org.parking.ParcelLoader.readPackages;
@Slf4j
public class ParcelMain {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            log.error("Пожалуйста, укажите путь к файлу в качестве аргумента.");
            return;
        }

        String filePath = args[0]; // Получаем путь из параметров командной строки
        List<Parcel> parcels = readPackages(filePath);
        Truck truck = packPackages(parcels);
        truck.print();
    }
}