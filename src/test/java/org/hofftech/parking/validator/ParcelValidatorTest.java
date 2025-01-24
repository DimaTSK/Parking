package org.hofftech.parking.validator;

import org.hofftech.parking.exception.ValidateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ParcelValidatorTest {

    private ParcelValidator parcelValidator;

    @BeforeEach
    void setUp() {
        parcelValidator = new ParcelValidator();
    }

    @Test
    void testValidateFile_EmptyList() {
        List<String> lines = Collections.emptyList();

        assertThatThrownBy(() -> parcelValidator.validateFile(lines))
                .isInstanceOf(ValidateException.class)
                .hasMessage("Файл пустой или не содержит данных.")
                .withFailMessage("Должен выбрасываться ValidateException для пустого списка строк");
    }


    @Test
    void testParseForm_NullForm() {
        String form = null;

        assertThatThrownBy(() -> parcelValidator.parseForm(form))
                .isInstanceOf(ValidateException.class)
                .hasMessage("Форма посылки не указана.")
                .withFailMessage("Должен выбрасываться ValidateException для null формы");
    }

    /**
     * Тестирует, что метод {@code validateForm} выбрасывает {@code ValidateException},
     * когда форма пуста.
     */
    @Test
    void testParseForm_EmptyForm() {
        String form = "";

        assertThatThrownBy(() -> parcelValidator.parseForm(form))
                .isInstanceOf(ValidateException.class)
                .hasMessage("Форма посылки не указана.")
                .withFailMessage("Должен выбрасываться ValidateException для пустой формы");
    }

    /**
     * Тестирует, что метод {@code validateForm} корректно парсит форму без разделителя и возвращает список с одной строкой.
     */
    @Test
    void testParseForm_SingleLineForm() {
        String form = "XXX\nX X\nXXX";

        List<String> expected = List.of("XXX\nX X\nXXX");

        List<String> result = parcelValidator.parseForm(form);

        assertThat(result)
                .as("Метод должен возвращать список с одной строкой для формы без разделителя")
                .isEqualTo(expected);
    }

    /**
     * Тестирует, что метод {@code validateForm} корректно парсит мультистрочную форму с разделителем и возвращает список строк.
     */
    @Test
    void testParseForm_MultiLineForm_Valid() {
        String form = "XXX,X X,XXX";

        List<String> expected = Arrays.asList("XXX", "X X", "XXX");

        List<String> result = parcelValidator.parseForm(form);

        assertThat(result)
                .as("Метод должен корректно парсить форму с разделителем")
                .isEqualTo(expected);
    }

    /**
     * Тестирует, что метод {@code validateForm} выбрасывает {@code ValidateException},
     * когда форма с разделителем содержит символы, которые "висят в воздухе".
     */
    @Test
    void testParseForm_MultiLineForm_InvalidDiagonalTouch() {
        String form = " X ,X X,   ";

        assertThatThrownBy(() -> parcelValidator.parseForm(form))
                .isInstanceOf(ValidateException.class)
                .hasMessage("Символ в позиции (0, 1) висит в воздухе.")
                .withFailMessage("Должен выбрасываться ValidateException для формы с некорректным касанием символов");
    }

    /**
     * Тестирует, что метод {@code validateForm} успешно обрабатывает форму с несколькими строками без ошибок.
     */
    @Test
    void testParseForm_MultiLineForm_NoDiagonalTouchIssues() {
        String form = "XXX,X X,XXX,X X,XXX";

        List<String> expected = Arrays.asList("XXX", "X X", "XXX", "X X", "XXX");

        List<String> result = parcelValidator.parseForm(form);

        assertThat(result)
                .as("Метод должен корректно парсить форму с несколькими строками без ошибок касания символов")
                .isEqualTo(expected);
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

        assertThat(result)
                .as("Метод должен корректно обрабатывать формы без горизонтальных связей, но с валидными вертикальными")
                .isEqualTo(expected);
    }

    /**
     * Тестирует, что метод {@code validateForm} выбрасывает {@code ValidateException},
     * когда входная форма содержит неверные разделители.
     */
    @Test
    void testParseForm_InvalidFormSplitter() {
        String form = "XXX;X X;XXX"; // Использован неверный разделитель ';' вместо ','

        List<String> expected = List.of("XXX;X X;XXX");

        List<String> result = parcelValidator.parseForm(form);

        assertThat(result)
                .as("Метод должен возвращать список с одной строкой при отсутствии правильного разделителя")
                .isEqualTo(expected);
    }
}
