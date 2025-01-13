package org.hofftech.parking.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.handler.DefaultCommandHandler;

import java.util.Scanner;

@Slf4j
@RequiredArgsConstructor
public class ConsoleListener {

    private final DefaultCommandHandler defaultCommandHandler;

    public void listen() {
        try (Scanner scanner = new Scanner(System.in)) {
            log.info("Ожидание команды пользователя...");
            System.out.print("Введите команду: ");

            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                defaultCommandHandler.handle(command);
            }
        } catch (Exception e) {
            log.error("Ошибка во время работы консольного контроллера: {}", e.getMessage(), e);
        }
    }

}