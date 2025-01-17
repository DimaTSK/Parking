package org.hofftech.parking.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Утилитный класс {@code FileSavingUtil} предоставляет методы для сохранения
 * информации о посылках в файл. Класс содержит статический метод для записи
 * списка посылок в указанный файл с возможностью сохранения данных с подсчетом
 * количества или без него.
 * <p>
 * Этот класс является финальным и содержит только статические методы, предназначенные
 * для облегчения процесса сохранения данных о посылках.
 * </p>
 *
 * <p><strong>Пример использования:</strong></p>
 * <pre>
 *     List<Map.Entry<String, Long>> packages = ...; // инициализация списка
 *     String outputPath = "path/to/output/file.txt";
 *     boolean withCount = true;
 *
 *     try {
 *         FileSavingUtil.savePackagesToFile(packages, outputPath, withCount);
 *     } catch (IOException e) {
 *         // обработка исключения
 *     }
 * </pre>
 *
 * @author
 * @version 1.0
 */
@Slf4j
public final class FileSavingUtil {

    /**
     * Предотвращает создание экземпляров данного класса.
     */
    private FileSavingUtil() {
        // Конструктор приватный для предотвращения создания экземпляров
    }

    /**
     * Сохраняет список посылок в файл по указанному пути.
     * <p>
     * Метод позволяет сохранить информацию о посылках либо с подсчетом количества каждой
     * посылки, либо без подсчета, записывая каждую посылку отдельно столько раз, сколько указано.
     * </p>
     *
     * @param packages       список посылок, каждая посылка представлена парой ключ-значение,
     *                       где ключ — название посылки, а значение — количество таких посылок
     * @param outputFilePath путь к файлу, в который будут сохранены данные
     * @param withCount      флаг, определяющий режим сохранения:
     *                       <ul>
     *                           <li>{@code true} — сохранять с подсчетом количества (например, "ПосылкаA" - 5 шт)</li>
     *                           <li>{@code false} — сохранять без подсчета, записывая каждую посылку отдельно</li>
     *                       </ul>
     * @throws IOException если произошла ошибка при записи в файл
     *
     * @implNote Метод использует {@link BufferedWriter} для эффективной записи данных в файл.
     *           В случае ошибки записи логируется сообщение об ошибке и исключение пробрасывается дальше.
     */
    public static void savePackagesToFile(List<Map.Entry<String, Long>> packages,
                                          String outputFilePath, boolean withCount) throws IOException {
        File outputFile = new File(outputFilePath);
        log.info("Начинаем запись в файл {} количества", withCount ? "с подсчётом" : "без подсчёта");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map.Entry<String, Long> entry : packages) {
                String line;
                if (withCount) {
                    line = String.format("\"%s\" - %d шт", entry.getKey(), entry.getValue());
                    writer.write(line);
                    writer.newLine();
                } else {
                    for (long i = 0; i < entry.getValue(); i++) { // Изменено на long для соответствия типу значения
                        line = String.format("\"%s\"", entry.getKey());
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            log.error("Ошибка при записи в файл {}: {}", outputFilePath, e.getMessage());
            throw e;
        }
        log.info("Посылки успешно импортированы и сохранены в файл: {}", outputFile.getAbsolutePath());
    }

}
