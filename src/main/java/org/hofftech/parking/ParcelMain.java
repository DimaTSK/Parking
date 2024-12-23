package org.hofftech.parking;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.processor.CommandProcessor;
import org.hofftech.parking.processor.ConsoleCommandProcessor;
import org.hofftech.parking.listener.ConsoleListener;
import org.hofftech.parking.service.*;
import org.hofftech.parking.utill.FileParser;
import org.hofftech.parking.utill.FileReader;
import org.hofftech.parking.utill.ParcelValidator;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;



@Slf4j
public class ParcelMain {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        log.info("Программа начала работу.");

        ParcelService parcelService = new ParcelService();
        TruckService truckService = new TruckService(parcelService);
        ParcelValidator parcelValidator = new ParcelValidator();
        FileReader fileReader = new FileReader();
        FileParser fileParser = new FileParser();
        JsonProcessingService jsonProcessingService = new JsonProcessingService(parcelValidator);
        FileProcessingService fileProcessingService = new FileProcessingService(fileReader, fileParser, parcelValidator, truckService, jsonProcessingService);
        CommandProcessor commandHandler = new ConsoleCommandProcessor(fileProcessingService, jsonProcessingService);

        ConsoleListener consoleListener = new ConsoleListener(commandHandler);
        consoleListener.listen();

        log.info("Программа завершила работу.");
    }
}