package org.hofftech.parking.listener;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.processor.CommandProcessor;

import java.util.Scanner;

@Slf4j
public class ConsoleListener {
    private final CommandProcessor commandHandler;
    private final Scanner scanner;

    public ConsoleListener(CommandProcessor commandHandler, Scanner scanner) {
        this.commandHandler = commandHandler;
        this.scanner = scanner;
    }

    public void listen() {
        log.info("Ждем команду пользователя");
        System.out.print("Введите путь к файлу:");

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            commandHandler.handle(command);
        }
    }
}