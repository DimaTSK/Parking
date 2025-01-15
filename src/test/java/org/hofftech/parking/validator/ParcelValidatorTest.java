package org.hofftech.parking.validator;

import org.hofftech.parking.validator.ParcelValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для {@link ParcelValidator}.
 * <p>
 * Этот класс проверяет методы {@link ParcelValidator#isValidFile(List)} и
 * {@link ParcelValidator#parseAndValidateForm(String)}, включая различные сценарии
 * использования и обработки ошибок.
 * </p>
 */
class ParcelValidatorTest {

    private ParcelValidator parcelValidator;

    @BeforeEach
    void setUp() {
        parcelValidator = new ParcelValidator();
    }


    /**
     * Тестирует, что метод {@code isValidFile} возвращает {@code false}, когда список строк равен {@code null}.
     */
    @Test
    void testIsValidFile_NullInput() {
        List<String> lines = null;

        boolean result = parcelValidator.isValidFile(lines);

        assertFalse(result, "Метод должен вернуть false для null входа");
    }

    /**
     * Тестирует, что метод {@code isValidFile} возвращает {@code false}, когда список строк пуст.
     */
    @Test
    void testIsValidFile_EmptyList() {
        List<String> lines = Collections.emptyList();

        boolean result = parcelValidator.isValidFile(lines);

        assertFalse(result, "Метод должен вернуть false для пустого списка строк");
    }

    /**
     * Тестирует, что метод {@code isValidFile} возвращает {@code true}, когда список строк не пуст.
     */
    @Test
    void testIsValidFile_NonEmptyList() {
        List<String> lines = Arrays.asList("Строка 1", "Строка 2", "Строка 3");

        boolean result = parcelValidator.isValidFile(lines);

        assertTrue(result, "Метод должен вернуть true для непустого списка строк");
    }

    // --- Тесты для метода parseAndValidateForm ---

    /**
     * Тестирует, что метод {@code parseAndValidateForm} выбрасывает {@code IllegalArgumentException},
     * когда форма равна {@code null}.
     */
    @Test
    void testParseAndValidateForm_NullForm() {
        String form = null;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ParcelValidator.parseAndValidateForm(form),
                "Должен выбрасываться IllegalArgumentException для null формы"
        );

        assertEquals("Форма посылки не указана.", exception.getMessage(),
                "Сообщение исключения должно совпадать с ожидаемым");
    }

    /**
     * Тестирует, что метод {@code parseAndValidateForm} выбрасывает {@code IllegalArgumentException},
     * когда форма пуста.
     */
    @Test
    void testParseAndValidateForm_EmptyForm() {
        String form = "";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ParcelValidator.parseAndValidateForm(form),
                "Должен выбрасываться IllegalArgumentException для пустой формы"
        );

        assertEquals("Форма посылки не указана.", exception.getMessage(),
                "Сообщение исключения должно совпадать с ожидаемым");
    }

    /**
     * Тестирует, что метод {@code parseAndValidateForm} корректно парсит форму без разделителя и возвращает список с одной строкой.
     */
    @Test
    void testParseAndValidateForm_SingleLineForm() {
        String form = "XXX\nX X\nXXX";

        List<String> expected = List.of("XXX\nX X\nXXX");

        List<String> result = ParcelValidator.parseAndValidateForm(form);

        assertEquals(expected, result, "Метод должен возвращать список с одной строкой для формы без разделителя");
    }

    /**
     * Тестирует, что метод {@code parseAndValidateForm} корректно парсит мультистрочную форму с разделителем и возвращает список строк.
     */
    @Test
    void testParseAndValidateForm_MultiLineForm_Valid() {
        String form = "XXX,X X,XXX";

        List<String> expected = Arrays.asList("XXX", "X X", "XXX");

        List<String> result = ParcelValidator.parseAndValidateForm(form);

        assertEquals(expected, result, "Метод должен корректно парсить форму с разделителем");
    }

    /**
     * Тестирует, что метод {@code parseAndValidateForm} выбрасывает {@code IllegalArgumentException},
     * когда форма с разделителем содержит символы, которые "висят в воздухе".
     */
    @Test
    void testParseAndValidateForm_MultiLineForm_InvalidDiagonalTouch() {
        String form = " X ,X X,   ";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ParcelValidator.parseAndValidateForm(form),
                "Должен выбрасываться IllegalArgumentException для формы с некорректным касанием символов"
        );

        assertEquals("Символ в позиции (0, 1) висит в воздухе.", exception.getMessage(),
                "Сообщение исключения должно отражать ошибку касания символов");
    }

    /**
     * Тестирует, что метод {@code parseAndValidateForm} успешно обрабатывает форму с несколькими строками без ошибок.
     */
    @Test
    void testParseAndValidateForm_MultiLineForm_NoDiagonalTouchIssues() {
        String form = "XXX,X X,XXX,X X,XXX";

        List<String> expected = Arrays.asList("XXX", "X X", "XXX", "X X", "XXX");

        List<String> result = ParcelValidator.parseAndValidateForm(form);

        assertEquals(expected, result, "Метод должен корректно парсить форму с несколькими строками без ошибок касания символов");
    }

}