package org.parking;

import lombok.extern.slf4j.Slf4j;
import org.parking.model.dto.ParcelDto;
import org.parking.model.dto.TruckCapacityDto;
import org.parking.service.ParcelPackerService;
import org.parking.service.TruckGridService;
import org.parking.service.TruckService;
import org.parking.utill.ParcelReader;

import java.io.IOException;
import java.util.List;

@Slf4j
public class ParcelMain {
    public static void main(String[] args) {
        log.info("Программа начала работу.");
        if (args.length == 0) {
            log.error("Пожалуйста, укажите путь к файлу в качестве аргумента.");
            return;
        }
        String filePath = args[0];

        TruckCapacityDto capacity = new TruckCapacityDto(6, 6);
        TruckGridService truckGridService = new TruckGridService(capacity.width(), capacity.height());
        TruckService truckService = new TruckService(capacity, truckGridService);
        ParcelPackerService parcelPacker = new ParcelPackerService(truckService);
        ParcelReader parcelReader = new ParcelReader();

        try {
            List<ParcelDto> parcelDtos = parcelReader.readPackages(filePath);
            parcelPacker.packPackages(parcelDtos);
            truckService.print();
        } catch (IOException e) {
            log.error("Ошибка при чтении пакетов из файла: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Произошла ошибка: {}", e.getMessage());
        }

        log.info("Программа завершила работу.");
    }
}