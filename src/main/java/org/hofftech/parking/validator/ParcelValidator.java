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

    /**
     * Проверяет, не висят ли символы ниже формы.
     *
     * <p>
     * Для каждого символа в текущей строке проверяется наличие соседних
     * символов слева, справа и снизу. Если символ не имеет поддержки
     * слева и снизу, либо справа и снизу, выбрасывается исключение {@code ValidateException}.
     * </p>
     *
     * @param currentRow текущая строка формы
     * @param nextRow    следующая строка формы
     * @param i          индекс текущей строки
     * @throws ValidateException если символ висит в воздухе
     */
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

    /**
     * Валидирует форму посылки.
     *
     * <p>
     * Проверяет, что форма не пустая и разделена корректным разделителем.
     * Затем выполняет дополнительную валидацию на касание диагоналей.
     * </p>
     *
     * @param form строковое представление формы посылки
     * @return список строк, представляющих строки формы
     * @throws ValidateException если форма пустая или имеет некорректный формат
     */
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

    /**
     * Валидирует касание диагоналей в форме посылки.
     *
     * <p>
     * Проверяет, что символы в каждой строке корректно соприкасаются с символами
     * в следующей строке, чтобы избежать висящих символов.
     * </p>
     *
     * @param rows список строк формы посылки
     * @throws ValidateException если обнаружены висящие символы
     */
    public void validateDiagonalTouch(List<String> rows) {
        int height = rows.size();

        for (int i = FIRST_ROW_INDEX; i < height - 1; i++) {
            String currentRow = rows.get(i);
            String nextRow = rows.get(i + 1);

            checkSymbolsBelowShape(currentRow, nextRow, i);
        }
    }

    /**
     * Валидирует содержимое файла с данными формы.
     *
     * <p>
     * Проверяет, что файл не пустой и содержит данные. При успешной проверке
     * выводит информацию в лог о количестве строк в файле.
     * </p>
     *
     * @param lines список строк из файла
     * @throws ValidateException если файл пустой или не содержит данных
     */
    public void validateFile(List<String> lines) {
        if (lines.isEmpty()) {
            throw new ValidateException("Файл пустой или не содержит данных.");
        }
        log.info("Файл успешно проверен. Количество строк: {}", lines.size());
    }
}