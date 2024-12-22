package org.hofftech.parking;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.handler.CommandHandler;
import org.hofftech.parking.handler.ConsoleCommandHandler;
import org.hofftech.parking.service.*;
import org.hofftech.parking.utill.FileParserUtil;
import org.hofftech.parking.utill.FileReaderUtil;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


@Slf4j
public class ParcelMain {
    private final CommandHandler commandHandler;

    public ParcelMain() {
        log.info("Создаем зависимости...");
        PackingService packingService = new PackingService();
        TruckService truckService = new TruckService(packingService);
        ValidatorService validatorService = new ValidatorService();
        FileReaderUtil fileReader = new FileReaderUtil();
        FileParserUtil fileParser = new FileParserUtil();
        JsonProcessingService jsonProcessingService = new JsonProcessingService(validatorService);
        FileProcessingService fileProcessingService =
                new FileProcessingService(fileReader, fileParser, validatorService, truckService, jsonProcessingService);
        this.commandHandler = new ConsoleCommandHandler(fileProcessingService, jsonProcessingService);
    }

    public void listen() {
        Scanner scanner = new Scanner(System.in);
        log.info("Ожидание команды пользователя...");
        System.out.print("Введите import easyalgorithm [путь_к_файлу] или import [путь_к_файлу] или " +
                "save [путь_к_файлу] или importjson [путь_к_файлу] или import even [кол-во грузовиков] [путь_к_файлу]" +
                " для выхода используйте exit: ");

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            commandHandler.handle(command);
        }
        scanner.close();
    }

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        log.info("Приложение запускается...");

        ParcelMain parcelMain = new ParcelMain();
        parcelMain.listen();

        log.info("Приложение завершило работу.");
    }
}