package org.hofftech.parking;


import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.config.ApplicationConfig;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Точка входа приложения Parcel.
 * <p>
 * Этот класс инициализирует приложение, устанавливая поток вывода с кодировкой UTF-8,
 * фиксируя начало и завершение работы приложения, а также запускает слушатель консоли
 * для обработки пользовательских взаимодействий или команд.
 * </p>
 * <p>
 * Использование:
 * <pre>
 *     java ParcelMain
 * </pre>
 * </p>
 *
 * @author
 * @version 1.0
 * @since 2023-10
 */
@Slf4j
public class ParcelMain {

    /**
     * Основной метод, служащий точкой входа для приложения Parcel.
     *
     * <p>
     * Этот метод выполняет следующие действия в указанном порядке:
     * <ul>
     *     <li>Устанавливает стандартный поток вывода с использованием кодировки UTF-8 для корректного отображения символов.</li>
     *     <li>Фиксирует информационное сообщение о запуске приложения.</li>
     *     <li>Инициализирует контекст приложения, создавая экземпляр {@link ApplicationConfig}.</li>
     *     <li>Получает слушатель консоли из контекста и начинает прослушивание ввода.</li>
     *     <li>Фиксирует информационное сообщение о завершении работы приложения.</li>
     * </ul>
     * </p>
     *
     * @param args аргументы командной строки, переданные приложению. В данном контексте не используются.
     */
    public static void main(String[] args) {
        // Устанавливаем поток вывода с кодировкой UTF-8
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        // Логируем начало работы приложения
        log.info("Приложение запускается...");

        // Инициализируем конфигурацию приложения
        ApplicationConfig context = new ApplicationConfig();

        // Запускаем прослушивание консольного ввода
        context.getConsoleListener().listen();

        // Логируем завершение работы приложения
        log.info("Приложение завершило работу.");
    }
}
