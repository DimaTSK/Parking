package org.hofftech.parking;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.processor.CommandProcessor;
import org.hofftech.parking.processor.ConsoleCommandProcessor;
import org.hofftech.parking.listener.ConsoleListener;
import org.hofftech.parking.service.*;
import org.hofftech.parking.utill.ParcelParser;
import org.hofftech.parking.utill.FileReader;
import org.hofftech.parking.utill.ParcelValidator;
import org.hofftech.parking.utill.TruckFactory;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


@Slf4j
public class ParcelMain {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        log.info("Программа начала работу.");

        ParcelService parcelService = new ParcelService();
        TruckFactory truckFactory = new TruckFactory();
        TruckService truckService = new TruckService(parcelService, truckFactory);
        ParcelValidator parcelValidator = new ParcelValidator();
        Scanner scanner = new Scanner(System.in);
        FileReader fileReader = new FileReader();
        ParcelParser parcelParser = new ParcelParser();
        JsonProcessingService jsonProcessingService = new JsonProcessingService(parcelValidator);
        FileProcessingService fileProcessingService = new FileProcessingService(fileReader, parcelParser, parcelValidator, truckService, jsonProcessingService);
        CommandProcessor commandProcessor = new ConsoleCommandProcessor(fileProcessingService, jsonProcessingService);

        ConsoleListener consoleListener = new ConsoleListener(commandProcessor, scanner);
        consoleListener.listen();

        log.info("Программа завершила работу.");
    }
}