package org.hofftech.parking;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.dto.ParcelDto;
import org.hofftech.parking.model.dto.TruckCapacityDto;
import org.hofftech.parking.service.TruckGridService;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.utill.ParcelReader;

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
        ParcelReader parcelReader = new ParcelReader();

        try {
            List<ParcelDto> parcelDtos = parcelReader.readPackages(filePath);
            truckService.packPackages(parcelDtos);
            truckGridService.print();
        } catch (IOException e) {
            log.error("Ошибка при чтении пакетов из файла: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Произошла ошибка: {}", e.getMessage());
        }

        log.info("Программа завершила работу.");
    }
}