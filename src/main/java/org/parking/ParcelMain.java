package org.parking;

import lombok.extern.slf4j.Slf4j;
import org.parking.model.dto.ParcelDto;
import org.parking.service.TruckService;

import java.io.IOException;
import java.util.List;

import static org.parking.service.ParcelLoaderService.packPackages;
import static org.parking.service.ParcelLoaderService.readPackages;
@Slf4j
public class ParcelMain {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            log.error("Пожалуйста, укажите путь к файлу в качестве аргумента.");
            return;
        }

        String filePath = args[0]; // Получаем путь из параметров командной строки
        List<ParcelDto> parcelDtos = readPackages(filePath);
        TruckService truckService = packPackages(parcelDtos);
        truckService.print();
    }
}