package org.hofftech.parking.listener;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.processor.CommandProcessor;

import java.util.Scanner;

@Slf4j
public class ConsoleListener {
    private final CommandProcessor commandHandler;

    public ConsoleListener(CommandProcessor commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void listen() {
        Scanner scanner = new Scanner(System.in);
        log.info("Ждем команду пользователя");
        System.out.print("Введите путь к файлу:");

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            commandHandler.handle(command);
        }
        scanner.close();
    }
}