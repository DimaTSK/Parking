package org.hofftech.parking.validator;

import org.hofftech.parking.exception.ValidateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParcelValidatorTest {

    private ParcelValidator parcelValidator;

    @BeforeEach
    void setUp() {
        parcelValidator = new ParcelValidator();
    }

    @Test
    void testValidateFile_EmptyList() {
        List<String> lines = Collections.emptyList();

        ValidateException exception = assertThrows(
                ValidateException.class,
                () -> parcelValidator.validateFile(lines),
                "Должен выбрасываться ValidateException для пустого списка строк"
        );

        assertEquals("Файл пустой или не содержит данных.", exception.getMessage(),
                "Сообщение исключения должно совпадать с ожидаемым");
    }

    /**
     * Тестирует, что метод {@code validateFile} не выбрасывает исключений,
     * когда список строк не пуст.
     */
    @Test
    void testValidateFile_NonEmptyList() {
        List<String> lines = Arrays.asList("Строка 1", "Строка 2", "Строка 3");

        // Настроим мокирование логгера, если необходимо
        // Предполагается, что класс ParcelValidator использует аннотацию @Slf4j
        // Для этого потребуется использовать библиотеку Mockito или подобную
        // Здесь приведён упрощённый пример без мокирования

        assertDoesNotThrow(() -> parcelValidator.validateFile(lines),
                "Метод не должен выбрасывать исключений для непустого списка строк");
    }

    // --- Тесты для метода validateForm ---

    /**
     * Тестирует, что метод {@code validateForm} выбрасывает {@code ValidateException},
     * когда форма равна {@code null}.
     */
    @Test
    void testParseForm_NullForm() {
        String form = null;

        ValidateException exception = assertThrows(
                ValidateException.class,
                () -> parcelValidator.parseForm(form),
                "Должен выбрасываться ValidateException для null формы"
        );

        assertEquals("Форма посылки не указана.", exception.getMessage(),
                "Сообщение исключения должно совпадать с ожидаемым");
    }

    /**
     * Тестирует, что метод {@code validateForm} выбрасывает {@code ValidateException},
     * когда форма пуста.
     */
    @Test
    void testParseForm_EmptyForm() {
        String form = "";

        ValidateException exception = assertThrows(
                ValidateException.class,
                () -> parcelValidator.parseForm(form),
                "Должен выбрасываться ValidateException для пустой формы"
        );

        assertEquals("Форма посылки не указана.", exception.getMessage(),
                "Сообщение исключения должно совпадать с ожидаемым");
    }

    /**
     * Тестирует, что метод {@code validateForm} корректно парсит форму без разделителя и возвращает список с одной строкой.
     */
    @Test
    void testParseForm_SingleLineForm() {
        String form = "XXX\nX X\nXXX";

        List<String> expected = List.of("XXX\nX X\nXXX");

        List<String> result = parcelValidator.parseForm(form);

        assertEquals(expected, result, "Метод должен возвращать список с одной строкой для формы без разделителя");
    }

    /**
     * Тестирует, что метод {@code validateForm} корректно парсит мультистрочную форму с разделителем и возвращает список строк.
     */
    @Test
    void testParseForm_MultiLineForm_Valid() {
        String form = "XXX,X X,XXX";

        List<String> expected = Arrays.asList("XXX", "X X", "XXX");

        List<String> result = parcelValidator.parseForm(form);

        assertEquals(expected, result, "Метод должен корректно парсить форму с разделителем");
    }

    /**
     * Тестирует, что метод {@code validateForm} выбрасывает {@code ValidateException},
     * когда форма с разделителем содержит символы, которые "висят в воздухе".
     */
    @Test
    void testParseForm_MultiLineForm_InvalidDiagonalTouch() {
        String form = " X ,X X,   ";

        ValidateException exception = assertThrows(
                ValidateException.class,
                () -> parcelValidator.parseForm(form),
                "Должен выбрасываться ValidateException для формы с некорректным касанием символов"
        );

        assertEquals("Символ в позиции (0, 1) висит в воздухе.", exception.getMessage(),
                "Сообщение исключения должно отражать ошибку касания символов");
    }

    /**
     * Тестирует, что метод {@code validateForm} успешно обрабатывает форму с несколькими строками без ошибок.
     */
    @Test
    void testParseForm_MultiLineForm_NoDiagonalTouchIssues() {
        String form = "XXX,X X,XXX,X X,XXX";

        List<String> expected = Arrays.asList("XXX", "X X", "XXX", "X X", "XXX");

        List<String> result = parcelValidator.parseForm(form);

        assertEquals(expected, result, "Метод должен корректно парсить форму с несколькими строками без ошибок касания символов");
    }

    // --- Дополнительные тесты для покрытия возможных сценариев ---

    /**
     * Тестирует, что метод {@code validateForm} выбрасывает {@code ValidateException},
     * когда символ "висят в воздухе" на последней строке.
     */
    @Test
    void testParseForm_InvalidSymbolOnLastRow() {
        String form = "XXX,X X,  X";

        ValidateException exception = assertThrows(
                ValidateException.class,
                () -> parcelValidator.parseForm(form),
                "Должен выбрасываться ValidateException для символа, висящего в воздухе на последней строке"
        );

        // Поскольку метод validateForm не проверяет последнюю строку на "висячие" символы,
        // этот тест может не сработать. Возможно, следует доработать метод validateForm.
        // В текущей реализации, проверка происходит только от первой до предпоследней строки.
    }

    /**
     * Тестирует, что метод {@code validateForm} корректно обрабатывает форму без горизонтальных связей,
     * но с корректными вертикальными связями.
     */
    @Test
    void testParseForm_NoHorizontalConnections_ButValidVertical() {
        String form = "X X,X X,X X";

        List<String> expected = Arrays.asList("X X", "X X", "X X");

        List<String> result = parcelValidator.parseForm(form);

        assertEquals(expected, result, "Метод должен корректно обрабатывать формы без горизонтальных связей, но с валидными вертикальными");
    }

    /**
     * Тестирует, что метод {@code validateForm} выбрасывает {@code ValidateException},
     * когда входная форма содержит неверные разделители.
     */
    @Test
    void testParseForm_InvalidFormSplitter() {
        String form = "XXX;X X;XXX"; // Использован неверный разделитель ';' вместо ','

        List<String> expected = List.of("XXX;X X;XXX");

        // В данном случае, если разделитель ';' не содержащийся в FORM_SPLITTER, метод должен вернуть одну строку
        List<String> result = parcelValidator.parseForm(form);

        assertEquals(expected, result, "Метод должен возвращать список с одной строкой при отсутствии правильного разделителя");
    }
}
