package org.hofftech.parking.validator;

import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.ValidateException;

import java.util.List;

/**
 * Класс для валидации формы посылки.
 * <p>
 * Предоставляет методы для проверки корректности формы посылки, включая
 * проверку символов и структуры формы.
 * </p>
 * Логирование осуществляется с помощью аннотации {@code @Slf4j}.
 * </p>
 */
@Slf4j
public class ParcelValidator {

    private static final String FORM_SPLITTER = ",";
    private static final int FIRST_ROW_INDEX = 0;

    private static void checkSymbolsBelowShape(String currentRow, String nextRow, int i) {
        for (int j = FIRST_ROW_INDEX; j < currentRow.length(); j++) {
            char current = currentRow.charAt(j);

            if (current != ' ') {
                boolean hasLeft = j > 0 && currentRow.charAt(j - 1) != ' ';
                boolean hasRight = j < currentRow.length() - 1 && currentRow.charAt(j + 1) != ' ';
                boolean hasBottom = j < nextRow.length() && nextRow.charAt(j) != ' ';

                if ((!hasLeft && !hasBottom) || (!hasRight && !hasBottom)) {
                    throw new ValidateException("Символ в позиции (" + i + ", " + j + ") висит в воздухе.");
                }
            }
        }
    }

    public List<String> validateForm(String form) {
        if (form == null || form.isEmpty()) {
            throw new ValidateException("Форма посылки не указана.");
        }

        if (form.contains(FORM_SPLITTER)) {
            List<String> rows = List.of(form.split(FORM_SPLITTER));
            validateDiagonalTouch(rows);
            return rows;
        }

        return List.of(form);
    }

    public void validateDiagonalTouch(List<String> rows) {
        int height = rows.size();

        for (int i = FIRST_ROW_INDEX; i < height - 1; i++) {
            String currentRow = rows.get(i);
            String nextRow = rows.get(i + 1);

            checkSymbolsBelowShape(currentRow, nextRow, i);
        }
    }

    public void validateFile(List<String> lines) {
        if (lines.isEmpty()) {
            throw new ValidateException("Файл пустой или не содержит данных.");
        }
        log.info("Файл успешно проверен. Количество строк: {}", lines.size());
    }
}