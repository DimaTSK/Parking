package org.hofftech.parking.listener;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.handler.CommandHandler;

import java.util.Scanner;

@Slf4j
public class ConsoleListener {
    private final CommandHandler commandHandler;

    public ConsoleListener(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void listen() {
        Scanner scanner = new Scanner(System.in);
        log.info("Ждем команду пользователя");
        //import путь_к_файлу, save путь_к_файлу, importjson количество грузовиков путь_к_файлу, exit
        System.out.print("Введите путь к файлу:");

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            commandHandler.handle(command);
        }
        scanner.close();
    }
}