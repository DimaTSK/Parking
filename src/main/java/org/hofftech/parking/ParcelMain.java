package org.hofftech.parking;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.factory.TruckFactory;
import org.hofftech.parking.parcer.ParcelParser;
import org.hofftech.parking.processor.CommandProcessor;
import org.hofftech.parking.processor.ConsoleCommandProcessor;
import org.hofftech.parking.listener.ConsoleListener;
import org.hofftech.parking.service.*;
import org.hofftech.parking.util.*;
import org.hofftech.parking.mapper.TruckDataMapper;
import org.hofftech.parking.validator.ParcelValidator;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


@Slf4j
public class ParcelMain {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            log.info("Программа начала работу.");

            ParcelService parcelService = new ParcelService();
            TruckFactory truckFactory = new TruckFactory();
            ParcelDistributor parcelDistributor = new ParcelDistributor(parcelService);
            TruckService truckService = new TruckService(parcelDistributor, truckFactory, parcelService);
            ParcelValidator parcelValidator = new ParcelValidator();
            Scanner scanner = new Scanner(System.in);
            FileReader fileReader = new FileReader();
            ParcelParser parcelParser = new ParcelParser();

            JsonWriter jsonWriter = new JsonWriter();
            JsonReader jsonReader = new JsonReader(parcelValidator);
            TruckDataMapper truckDataMapper = new TruckDataMapper();
            JsonFileService jsonFileService = new JsonFileService(jsonWriter, jsonReader, truckDataMapper);

            TruckPrinter truckPrinter = new TruckPrinter();
            ParcelSorter parcelSorter = new ParcelSorter();

            FileProcessingService fileProcessingService = new FileProcessingService(
                    fileReader, parcelParser, parcelValidator, truckService, jsonFileService, truckPrinter, parcelSorter
            );

            CommandProcessor commandProcessor = new ConsoleCommandProcessor(fileProcessingService, jsonFileService);

            ConsoleListener consoleListener = new ConsoleListener(commandProcessor, scanner);
            consoleListener.listen();

            log.info("Программа завершила работу.");
        } catch (Exception e) {
            log.error("Произошла ошибка в основной программе: {}", e.getMessage(), e);
        }
    }
}