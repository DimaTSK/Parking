package org.hofftech.parking.util;


import org.hofftech.parking.util.FileReaderUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для {@link FileReaderUtil}.
 * <p>
 * Этот класс проверяет различные сценарии чтения файлов, включая успешное чтение,
 * обработку несуществующих файлов, файлов без прав на чтение и пустых файлов.
 * </p>
 */
class FileReaderUtilTest {

    @TempDir
    Path tempDir;

    /**
     * Тестирует успешное чтение файла с несколькими строками.
     */
    @Test
    void testReadAllLines_Success() throws IOException {
        Path tempFile = Files.createFile(tempDir.resolve("testFile.txt"));
        List<String> lines = Arrays.asList("Первая строка", "Вторая строка", "Третья строка");
        Files.write(tempFile, lines, StandardOpenOption.WRITE);


        List<String> readLines = FileReaderUtil.readAllLines(tempFile);


        assertEquals(lines, readLines, "Прочитанные строки должны совпадать с ожидаемыми");
    }

    /**
     * Тестирует чтение пустого файла.
     */
    @Test
    void testReadAllLines_EmptyFile() throws IOException {

        Path emptyFile = Files.createFile(tempDir.resolve("emptyFile.txt"));


        List<String> readLines = FileReaderUtil.readAllLines(emptyFile);

        assertTrue(readLines.isEmpty(), "Прочитанные строки должны быть пустыми");
    }

    /**
     * Тестирует поведение при возникновении IOException во время чтения.
     */
    @Test
    void testReadAllLines_IOException() throws IOException {

        Path tempFile = Files.createFile(tempDir.resolve("testFile.txt"));
        List<String> lines = Arrays.asList("Line1", "Line2");
        Files.write(tempFile, lines, StandardOpenOption.WRITE);


        Files.delete(tempFile);


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            FileReaderUtil.readAllLines(tempFile);
        });


        assertTrue(exception.getMessage().contains("Ошибка чтения файла"), "Сообщение об ошибке должно содержать 'Ошибка чтения файла'");
    }
}
