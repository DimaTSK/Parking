package org.hofftech.parking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Основной класс приложения для загрузки посылок.
 * <p>
 * Это Spring Boot приложение, которое инициализирует и запускает контекст приложения.
 * Логирование осуществляется с помощью аннотации {@code @Slf4j}.
 * </p>
 */
@Slf4j
@SpringBootApplication
public class ParcelLoadingApplication {

    /**
     * Точка входа в приложение.
     *
     * <p>
     * Устанавливает кодировку вывода в UTF-8, запускает контекст Spring Boot и
     * выводит сообщения о старте и завершении работы приложения.
     * </p>
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        SpringApplication.run(ParcelLoadingApplication.class, args);
    }
}

