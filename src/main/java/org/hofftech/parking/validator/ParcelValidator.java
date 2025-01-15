package org.hofftech.parking.validator;

import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * Класс {@code ParcelValidator} предоставляет методы для проверки валидности
 * файла с данными и формы посылки. Он осуществляет проверку содержимого файла на
 * наличие данных и валидирует форму посылки на корректность диагонального
 * касания символов.
 * <p>
 * Используется для валидации данных перед обработкой в системе парковки.
 * </p>
 *
 * @author
 * @version 1.0
 */
@Slf4j
public class ParcelValidator {

    /**
     * Разделитель, используемый для разделения строк в форме посылки.
     */
    private static final String DELIMITER = ",";

    /**
     * Проверяет, является ли предоставленный список строк валидным файлом.
     * <p>
     * Файл считается валидным, если он не {@code null} и содержит хотя бы одну строку.
     * </p>
     *
     * @param lines список строк, представляющих содержимое файла
     * @return {@code true}, если файл валиден; {@code false} в противном случае
     */
    public boolean isValidFile(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            log.error("Файл пустой или не содержит данных.");
            return false;
        }
        log.info("Файл успешно проверен. Количество строк: {}", lines.size());
        return true;
    }

    /**
     * Парсит и валидирует форму посылки.
     * <p>
     * Форма посылки должна быть строкой, возможно содержащей несколько строк,
     * разделенных разделителем {@code DELIMITER}. Если разделитель присутствует,
     * каждая строка будет проверена на корректность диагонального касания символов.
     * </p>
     *
     * @param form строковое представление формы посылки
     * @return список строк, полученных после парсинга формы
     * @throws IllegalArgumentException если форма посылки не указана или
     *                                  содержит некорректные символы
     */
    public static List<String> parseAndValidateForm(String form) {
        if (form == null || form.isEmpty()) {
            throw new IllegalArgumentException("Форма посылки не указана.");
        }

        if (form.contains(DELIMITER)) {
            List<String> rows = List.of(form.split(DELIMITER));
            validateDiagonalTouch(rows);
            return rows;
        }

        return List.of(form);
    }

    /**
     * Валидирует диагональное касание символов в списке строк.
     * <p>
     * Каждый символ в текущей строке проверяется на наличие соседних символов слева,
     * справа и снизу. Если символ "висят в воздухе" (не имеет левого или правого
     * соседа и не имеет нижнего соседа), выбрасывается {@code IllegalArgumentException}.
     * </p>
     *
     * @param rows список строк для валидации
     * @throws IllegalArgumentException если найден символ, который "висит в воздухе"
     */
    public static void validateDiagonalTouch(List<String> rows) {
        int height = rows.size();

        for (int i = 0; i < height - 1; i++) {
            String currentRow = rows.get(i);
            String nextRow = rows.get(i + 1);

            for (int j = 0; j < currentRow.length(); j++) {
                char current = currentRow.charAt(j);

                if (current != ' ') {
                    boolean hasLeft = j > 0 && currentRow.charAt(j - 1) != ' ';
                    boolean hasRight = j < currentRow.length() - 1 && currentRow.charAt(j + 1) != ' ';
                    boolean hasBottom = j < nextRow.length() && nextRow.charAt(j) != ' ';

                    if ((!hasLeft && !hasBottom) || (!hasRight && !hasBottom)) {
                        throw new IllegalArgumentException(
                                "Символ в позиции (" + i + ", " + j + ") висит в воздухе."
                        );
                    }
                }
            }
        }
    }
}
