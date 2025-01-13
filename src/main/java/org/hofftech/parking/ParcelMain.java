package org.hofftech.parking;


import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.config.ApplicationConfig;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ParcelMain {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        log.info("Приложение запускается...");
        ApplicationConfig context = new ApplicationConfig();
        context.getConsoleListener().listen();

        log.info("Приложение завершило работу.");
    }
}