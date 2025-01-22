package org.hofftech.parking.util;

import org.hofftech.parking.exception.InputFileException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
     * Тестирует поведение, когда файл не существует.
     */
    @Test
    void testReadAllLines_FileDoesNotExist() {
        Path nonExistentFile = tempDir.resolve("nonExistentFile.txt");

        InputFileException exception = assertThrows(InputFileException.class, () -> {
            FileReaderUtil.readAllLines(nonExistentFile);
        });

        assertTrue(exception.getMessage().contains("Файл не существует"),
                "Сообщение об ошибке должно содержать 'Файл не существует'");
    }
}