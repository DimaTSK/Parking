package org.hofftech.parking.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.handler.CommandHandler;

import java.util.Scanner;

/**
 * Консольный слушатель для обработки пользовательских команд.
 * <p>
 * Этот класс использует {@link Scanner} для чтения ввода пользователя из консоли
 * и передает команды {@link CommandHandler} для дальнейшей обработки.
 * </p>
 *
 * @author
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class ConsoleListener {

    /**
     * Обработчик команд, отвечающий за выполнение введенных пользователем команд.
     */
    private final CommandHandler commandHandler;

    /**
     * Начинает прослушивание консольного ввода для получения команд от пользователя.
     * <p>
     * Метод ожидает ввода команды, считывает её и передаёт {@link CommandHandler}
     * для обработки. В случае возникновения ошибки, она логируется.
     * </p>
     */
    public void listen() {
        try (Scanner scanner = new Scanner(System.in)) {
            log.info("Ожидание команды пользователя...");
            System.out.print("Введите команду: ");

            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                commandHandler.handle(command);
            }
        } catch (Exception e) {
            log.error("Ошибка во время работы консольного контроллера: {}", e.getMessage(), e);
        }
    }

}
